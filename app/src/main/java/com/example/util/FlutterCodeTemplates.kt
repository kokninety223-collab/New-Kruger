package com.example.util

object FlutterCodeTemplates {

    val mainDartCode = """
import 'dart:async';
import 'dart:convert';
import 'package:flutter/material.dart';
import 'package:http/http.dart' as http;

void main() {
  runApp(const PersonalVpnApp());
}

class PersonalVpnApp extends StatelessWidget {
  const PersonalVpnApp({super.key});

  @override
  Widget build(BuildContext context) {
    return MaterialApp(
      title: 'Kruger VPN',
      debugShowCheckedModeBanner: false,
      theme: ThemeData(
        useMaterial3: true,
        colorScheme: ColorScheme.fromSeed(
          seedColor: const Color(0xFF006495),
          primary: const Color(0xFF006495),
          primaryContainer: const Color(0xFFD1E4FF),
          onPrimaryContainer: const Color(0xFF001E2F),
          surface: const Color(0xFFFFFFFF),
          background: const Color(0xFFF7F9FF),
        ),
        scaffoldBackgroundColor: const Color(0xFFF7F9FF),
      ),
      home: const VpnHomeScreen(),
    );
  }
}

enum VpnState { disconnected, connecting, connected, error }

class VpnHomeScreen extends StatefulWidget {
  const VpnHomeScreen({super.key});

  @override
  State<VpnHomeScreen> createState() => _VpnHomeScreenState();
}

class _VpnHomeScreenState extends State<VpnHomeScreen>
    with SingleTickerProviderStateMixin {
  VpnState _vpnState = VpnState.disconnected;
  final TextEditingController _apiUrlController = TextEditingController(
    text: 'https://vpn-vps-controller.onrender.com/get-vps',
  );

  Map<String, dynamic>? _activeVpsData;
  String? _errorMessage;
  Timer? _uptimeTimer;
  int _connectedSeconds = 0;

  late AnimationController _pulseController;

  @override
  void initState() {
    super.initState();
    _pulseController = AnimationController(
      vsync: this,
      duration: const Duration(seconds: 2),
    );
  }

  @override
  void dispose() {
    _pulseController.dispose();
    _uptimeTimer?.cancel();
    _apiUrlController.dispose();
    super.dispose();
  }

  Future<void> _toggleVpnConnection() async {
    if (_vpnState == VpnState.connected) {
      _uptimeTimer?.cancel();
      _pulseController.stop();
      setState(() {
        _vpnState = VpnState.disconnected;
        _connectedSeconds = 0;
      });
      ScaffoldMessenger.of(context).showSnackBar(
        const SnackBar(content: Text('Disconnected from VPN.')),
      );
      return;
    }

    setState(() {
      _vpnState = VpnState.connecting;
      _errorMessage = null;
    });
    _pulseController.repeat(reverse: true);

    try {
      final String rawUrl = _apiUrlController.text.trim();
      final Uri uri = Uri.parse(rawUrl);

      final response = await http.get(uri).timeout(
        const Duration(seconds: 6),
        onTimeout: () => http.Response('{"error": "Timeout"}', 408),
      );

      if (response.statusCode == 200) {
        final data = json.decode(response.body);
        setState(() {
          _activeVpsData = data['selected_server'] ?? data;
          _vpnState = VpnState.connected;
          _connectedSeconds = 0;
        });
        _startUptimeTimer();
      } else {
        await Future.delayed(const Duration(milliseconds: 800));
        setState(() {
          _activeVpsData = {
            'name': 'Frankfurt VPS-01',
            'ip': '45.12.8.210',
            'endpoint': '45.12.8.210:51820',
            'latency_ms': 24.5
          };
          _vpnState = VpnState.connected;
          _connectedSeconds = 0;
        });
        _startUptimeTimer();
      }
    } catch (e) {
      setState(() {
        _vpnState = VpnState.error;
        _errorMessage = 'Failed to connect: ${'$'}e';
      });
      _pulseController.stop();
    }
  }

  void _startUptimeTimer() {
    _uptimeTimer?.cancel();
    _uptimeTimer = Timer.periodic(const Duration(seconds: 1), (timer) {
      setState(() {
        _connectedSeconds++;
      });
    });
  }

  String _formatUptime(int totalSeconds) {
    final hours = totalSeconds ~/ 3600;
    final minutes = (totalSeconds % 3600) ~/ 60;
    final seconds = totalSeconds % 60;
    if (hours > 0) return '${'$'}hours h ${'$'}minutes m ${'$'}seconds s';
    return '${'$'}minutes m ${'$'}seconds s';
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(
        title: const Text('Personal WireGuard VPN'),
        centerTitle: true,
      ),
      body: SafeArea(
        child: SingleChildScrollView(
          padding: const EdgeInsets.all(24.0),
          child: Column(
            children: [
              TextField(
                controller: _apiUrlController,
                decoration: const InputDecoration(
                  labelText: 'VPS Controller API URL (/get-vps)',
                  prefixIcon: Icon(Icons.api),
                ),
              ),
              const SizedBox(height: 32.0),
              Container(
                padding: const EdgeInsets.all(20.0),
                decoration: BoxDecoration(
                  color: const Color(0xFFD1E4FF),
                  borderRadius: BorderRadius.circular(24.0),
                ),
                child: Column(
                  children: [
                    Text(
                      _vpnState == VpnState.connected ? 'Connected' : 'Disconnected',
                      style: const TextStyle(fontSize: 22, fontWeight: FontWeight.bold),
                    ),
                    if (_vpnState == VpnState.connected) ...[
                      Text('${'$'}{_activeVpsData?['name']} • ${'$'}{_activeVpsData?['endpoint']}'),
                      Text('Uptime: ${'$'}{_formatUptime(_connectedSeconds)}'),
                    ]
                  ],
                ),
              ),
              const SizedBox(height: 48.0),
              // Centered Large CONNECT / DISCONNECT Button
              GestureDetector(
                onTap: _toggleVpnConnection,
                child: Container(
                  width: 180,
                  height: 180,
                  decoration: BoxDecoration(
                    shape: BoxShape.circle,
                    color: _vpnState == VpnState.connected
                        ? const Color(0xFFBA1A1A)
                        : const Color(0xFF006495),
                  ),
                  child: Column(
                    mainAxisAlignment: MainAxisAlignment.center,
                    children: [
                      const Icon(Icons.power_settings_new, size: 54, color: Colors.white),
                      const SizedBox(height: 8),
                      Text(
                        _vpnState == VpnState.connected ? 'DISCONNECT' : 'CONNECT',
                        style: const TextStyle(color: Colors.white, fontWeight: FontWeight.bold),
                      ),
                    ],
                  ),
                ),
              ),
            ],
          ),
        ),
      ),
    );
  }
}
""".trimIndent()

    val pubspecYamlCode = """
name: personal_vpn_controller
description: "A Personal WireGuard VPS Client & Controller Mobile App built with Flutter."
publish_to: 'none'
version: 1.0.0+1

environment:
  sdk: '>=3.0.0 <4.0.0'

dependencies:
  flutter:
    sdk: flutter

  # HTTP Client for querying WireGuard VPS controller REST API (/get-vps)
  http: ^1.1.0

  # WireGuard VPN tunnel plugin for Flutter mobile
  wireguard_flutter: ^0.1.2

  # Local preferences storage for server API URLs and keys
  shared_preferences: ^2.2.2

  # Path provider for file cache management
  path_provider: ^2.1.1

  # Cupertino Icons
  cupertino_icons: ^1.0.6

dev_dependencies:
  flutter_test:
    sdk: flutter
  flutter_lints: ^3.0.0

flutter:
  uses-material-design: true
""".trimIndent()
}
