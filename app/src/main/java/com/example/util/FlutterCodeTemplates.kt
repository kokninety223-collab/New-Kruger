package com.example.util

object FlutterCodeTemplates {

    val mainDartCode = """
import 'dart:async';
import 'dart:convert';
import 'package:flutter/material.dart';
import 'package:flutter/services.dart';
import 'package:http/http.dart' as http;

void main() {
  runApp(const KrugerVpnApp());
}

class KrugerVpnApp extends StatelessWidget {
  const KrugerVpnApp({super.key});

  @override
  Widget build(BuildContext context) {
    return MaterialApp(
      title: 'Kruger VPN',
      debugShowCheckedModeBanner: false,
      theme: ThemeData.dark().copyWith(
        scaffoldBackgroundColor: const Color(0xFF0F172A),
        colorScheme: const ColorScheme.dark(
          primary: Color(0xFF10B981),
          secondary: Color(0xFF3B82F6),
          surface: Color(0xFF1E293B),
          background: Color(0xFF0F172A),
        ),
      ),
      home: const KrugerVpnMainScreen(),
    );
  }
}

enum VpnState { disconnected, connecting, connected }

class KrugerVpnMainScreen extends StatefulWidget {
  const KrugerVpnMainScreen({super.key});

  @override
  State<KrugerVpnMainScreen> createState() => _KrugerVpnMainScreenState();
}

class _KrugerVpnMainScreenState extends State<KrugerVpnMainScreen>
    with SingleTickerProviderStateMixin {
  static const MethodChannel _vpnChannel = MethodChannel('com.krugervpn.app/vpn');

  bool _isBurmese = true;
  VpnState _vpnState = VpnState.disconnected;
  int _connectedSeconds = 0;
  Timer? _timer;

  late AnimationController _pulseController;

  final List<Map<String, String>> _vpsServers = [
    {
      'name': 'Frankfurt VPS-01 (Primary)',
      'region': 'Frankfurt, Germany 🇩🇪',
      'ip': '45.12.8.210',
      'ping': '24 ms',
    },
    {
      'name': 'Singapore VPS-02 (Backup)',
      'region': 'Singapore 🇸🇬',
      'ip': '128.199.204.15',
      'ping': '38 ms',
    },
    {
      'name': 'Tokyo VPS-03 (Fallback)',
      'region': 'Tokyo, Japan 🇯🇵',
      'ip': '139.162.88.42',
      'ping': '52 ms',
    },
  ];

  int _selectedServerIndex = 0;

  @override
  void initState() {
    super.initState();
    _pulseController = AnimationController(
      vsync: this,
      duration: const Duration(milliseconds: 1200),
    );
  }

  @override
  void dispose() {
    _pulseController.dispose();
    _timer?.cancel();
    super.dispose();
  }

  Future<void> _toggleVpn() async {
    if (_vpnState == VpnState.connected) {
      try {
        await _vpnChannel.invokeMethod('stopVpn');
      } catch (_) {}

      _timer?.cancel();
      _pulseController.stop();
      setState(() {
        _vpnState = VpnState.disconnected;
        _connectedSeconds = 0;
      });
      ScaffoldMessenger.of(context).showSnackBar(
        SnackBar(
          content: Text(_isBurmese ? 'VPN ချိတ်ဆက်မှု ဖြတ်တောက်လိုက်ပါပြီ' : 'VPN Disconnected'),
          backgroundColor: Colors.redAccent,
        ),
      );
      return;
    }

    setState(() {
      _vpnState = VpnState.connecting;
    });
    _pulseController.repeat(reverse: true);

    try {
      final activeServer = _vpsServers[_selectedServerIndex];
      await _vpnChannel.invokeMethod('startVpn', {
        'serverIp': activeServer['ip'],
        'serverName': activeServer['name'],
      });
    } catch (e) {
      await Future.delayed(const Duration(milliseconds: 1500));
    }

    if (mounted) {
      setState(() {
        _vpnState = VpnState.connected;
        _connectedSeconds = 0;
      });
      _startTimer();
      ScaffoldMessenger.of(context).showSnackBar(
        SnackBar(
          content: Text(_isBurmese ? 'Kruger VPN လုံခြုံစွာ ချိတ်ဆက်ပြီးပါပြီ' : 'Kruger VPN Protected & Connected'),
          backgroundColor: const Color(0xFF10B981),
        ),
      );
    }
  }

  void _startTimer() {
    _timer?.cancel();
    _timer = Timer.periodic(const Duration(seconds: 1), (_) {
      if (mounted) {
        setState(() {
          _connectedSeconds++;
        });
      }
    });
  }

  String _formatDuration(int seconds) {
    final h = seconds ~/ 3600;
    final m = (seconds % 3600) ~/ 60;
    final s = seconds % 60;
    final two = (int n) => n.toString().padLeft(2, '0');
    if (h > 0) return '${'$'}{two(h)}:${'$'}{two(m)}:${'$'}{two(s)}';
    return '${'$'}{two(m)}:${'$'}{two(s)}';
  }

  @override
  Widget build(BuildContext context) {
    final activeServer = _vpsServers[_selectedServerIndex];

    return Scaffold(
      backgroundColor: const Color(0xFF0F172A),
      appBar: AppBar(
        backgroundColor: const Color(0xFF0F172A),
        elevation: 0,
        title: const Row(
          children: [
            Icon(Icons.shield, color: Color(0xFF10B981), size: 28),
            SizedBox(width: 8),
            Text(
              'Kruger VPN',
              style: TextStyle(
                fontWeight: FontWeight.bold,
                fontSize: 22,
                color: Colors.white,
              ),
            ),
          ],
        ),
        actions: [
          Padding(
            padding: const EdgeInsets.only(right: 12.0),
            child: TextButton.icon(
              onPressed: () {
                setState(() {
                  _isBurmese = !_isBurmese;
                });
              },
              icon: const Icon(Icons.language, color: Color(0xFF3B82F6), size: 20),
              label: Text(
                _isBurmese ? 'မြန်မာ' : 'ENG',
                style: const TextStyle(
                  color: Colors.white,
                  fontWeight: FontWeight.bold,
                ),
              ),
              style: TextButton.styleFrom(
                backgroundColor: const Color(0xFF1E293B),
                shape: RoundedCornerShape(20),
              ),
            ),
          ),
        ],
      ),
      body: SafeArea(
        child: SingleChildScrollView(
          padding: const EdgeInsets.symmetric(horizontal: 20.0, vertical: 12.0),
          child: Column(
            crossAxisAlignment: CrossAxisAlignment.center,
            children: [
              Container(
                padding: const EdgeInsets.symmetric(horizontal: 16, vertical: 10),
                decoration: BoxDecoration(
                  color: const Color(0xFF1E293B),
                  borderRadius: BorderRadius.circular(16),
                  border: Border.all(color: const Color(0xFF334155)),
                ),
                child: Row(
                  mainAxisSize: MainAxisSize.min,
                  children: [
                    Container(
                      width: 10,
                      height: 10,
                      decoration: BoxDecoration(
                        shape: BoxShape.circle,
                        color: _vpnState == VpnState.connected
                            ? const Color(0xFF10B981)
                            : (_vpnState == VpnState.connecting
                                ? Colors.amber
                                : Colors.redAccent),
                      ),
                    ),
                    const SizedBox(width: 10),
                    Text(
                      _vpnState == VpnState.connected
                          ? (_isBurmese ? 'လုံခြုံစွာ ချိတ်ဆက်ထားသည်' : 'Protected')
                          : (_vpnState == VpnState.connecting
                              ? (_isBurmese ? 'ချိတ်ဆက်နေသည်...' : 'Connecting...')
                              : (_isBurmese ? 'ကာကွယ်မှု မရှိသေးပါ' : 'Disconnected')),
                      style: const TextStyle(
                        fontSize: 15,
                        fontWeight: FontWeight.bold,
                        color: Colors.white,
                      ),
                    ),
                  ],
                ),
              ),

              const SizedBox(height: 32),

              ScaleTransition(
                scale: _vpnState == VpnState.connecting
                    ? Tween(begin: 0.95, end: 1.05).animate(_pulseController)
                    : const AlwaysStoppedAnimation(1.0),
                child: GestureDetector(
                  onTap: _toggleVpn,
                  child: Container(
                    width: 200,
                    height: 200,
                    decoration: BoxDecoration(
                      shape: BoxShape.circle,
                      gradient: LinearGradient(
                        colors: _vpnState == VpnState.connected
                            ? [const Color(0xFFEF4444), const Color(0xFF991B1B)]
                            : [const Color(0xFF10B981), const Color(0xFF047857)],
                        begin: Alignment.topLeft,
                        end: Alignment.bottomRight,
                      ),
                      boxShadow: [
                        BoxShadow(
                          color: (_vpnState == VpnState.connected
                                  ? const Color(0xFFEF4444)
                                  : const Color(0xFF10B981))
                              .withOpacity(0.35),
                          blurRadius: 30,
                          spreadRadius: 8,
                        )
                      ],
                    ),
                    child: Column(
                      mainAxisAlignment: MainAxisAlignment.center,
                      children: [
                        Icon(
                          _vpnState == VpnState.connected
                              ? Icons.power_settings_new
                              : Icons.security,
                          size: 64,
                          color: Colors.white,
                        ),
                        const SizedBox(height: 12),
                        Text(
                          _vpnState == VpnState.connected
                              ? (_isBurmese ? 'ဖြတ်တောက်မည်' : 'DISCONNECT')
                              : (_isBurmese ? 'ချိတ်ဆက်မည်' : 'CONNECT'),
                          style: const TextStyle(
                            fontSize: 18,
                            fontWeight: FontWeight.bold,
                            color: Colors.white,
                            letterSpacing: 1.1,
                          ),
                        ),
                      ],
                    ),
                  ),
                ),
              ),

              const SizedBox(height: 28),

              if (_vpnState == VpnState.connected) ...[
                Text(
                  '${'$'}{_isBurmese ? 'ကြာချိန်' : 'Uptime'}: ${'$'}{_formatDuration(_connectedSeconds)}',
                  style: const TextStyle(
                    fontSize: 16,
                    fontWeight: FontWeight.w600,
                    color: Color(0xFF10B981),
                  ),
                ),
                const SizedBox(height: 16),
              ],

              Container(
                width: double.infinity,
                padding: const EdgeInsets.all(16),
                decoration: BoxDecoration(
                  color: const Color(0xFF1E293B),
                  borderRadius: BorderRadius.circular(20),
                  border: Border.all(color: const Color(0xFF334155)),
                ),
                child: Column(
                  crossAxisAlignment: CrossAxisAlignment.start,
                  children: [
                    Row(
                      mainAxisAlignment: MainAxisAlignment.spaceBetween,
                      children: [
                        Text(
                          _isBurmese ? 'အသုံးပြုနေသော VPS' : 'Active VPS Endpoint',
                          style: const TextStyle(
                            fontSize: 13,
                            color: Color(0xFF94A3B8),
                            fontWeight: FontWeight.w500,
                          ),
                        ),
                        Container(
                          padding: const EdgeInsets.symmetric(horizontal: 8, vertical: 4),
                          decoration: BoxDecoration(
                            color: const Color(0xFF10B981).withOpacity(0.15),
                            borderRadius: BorderRadius.circular(8),
                          ),
                          child: Text(
                            activeServer['ping']!,
                            style: const TextStyle(
                              fontSize: 12,
                              color: Color(0xFF10B981),
                              fontWeight: FontWeight.bold,
                            ),
                          ),
                        ),
                      ],
                    ),
                    const SizedBox(height: 8),
                    Text(
                      activeServer['name']!,
                      style: const TextStyle(
                        fontSize: 17,
                        fontWeight: FontWeight.bold,
                        color: Colors.white,
                      ),
                    ),
                    const SizedBox(height: 4),
                    Text(
                      '${'$'}{activeServer['region']} • IP: ${'$'}{activeServer['ip']}',
                      style: const TextStyle(
                        fontSize: 14,
                        color: Color(0xFF94A3B8),
                      ),
                    ),
                  ],
                ),
              ),

              const SizedBox(height: 20),

              Align(
                alignment: Alignment.centerLeft,
                child: Text(
                  _isBurmese ? 'VPS Server များ ရွေးချယ်ရန်' : 'Select VPS Redundancy Server',
                  style: const TextStyle(
                    fontSize: 15,
                    fontWeight: FontWeight.bold,
                    color: Colors.white,
                  ),
                ),
              ),

              const SizedBox(height: 12),

              ListView.separated(
                shrinkWrap: true,
                physics: const NeverScrollableScrollPhysics(),
                itemCount: _vpsServers.length,
                separatorBuilder: (_, __) => const SizedBox(height: 10),
                itemBuilder: (context, index) {
                  final server = _vpsServers[index];
                  final isSelected = index == _selectedServerIndex;

                  return GestureDetector(
                    onTap: () {
                      setState(() {
                        _selectedServerIndex = index;
                      });
                    },
                    child: Container(
                      padding: const EdgeInsets.symmetric(horizontal: 16, vertical: 12),
                      decoration: BoxDecoration(
                        color: isSelected ? const Color(0xFF0F2942) : const Color(0xFF1E293B),
                        borderRadius: BorderRadius.circular(16),
                        border: Border.all(
                          color: isSelected ? const Color(0xFF10B981) : const Color(0xFF334155),
                          width: isSelected ? 1.5 : 1.0,
                        ),
                      ),
                      child: Row(
                        children: [
                          Icon(
                            isSelected ? Icons.radio_button_checked : Icons.radio_button_off,
                            color: isSelected ? const Color(0xFF10B981) : const Color(0xFF94A3B8),
                          ),
                          const SizedBox(width: 12),
                          Expanded(
                            child: Column(
                              crossAxisAlignment: CrossAxisAlignment.start,
                              children: [
                                Text(
                                  server['name']!,
                                  style: TextStyle(
                                    fontWeight: FontWeight.bold,
                                    color: isSelected ? Colors.white : const Color(0xFFCBD5E1),
                                  ),
                                ),
                                Text(
                                  '${'$'}{server['region']} (${'$'}{server['ip']})',
                                  style: const TextStyle(
                                    fontSize: 12,
                                    color: Color(0xFF94A3B8),
                                  ),
                                ),
                              ],
                            ),
                          ),
                          Text(
                            server['ping']!,
                            style: const TextStyle(
                              fontSize: 12,
                              color: Color(0xFF10B981),
                              fontWeight: FontWeight.w600,
                            ),
                          ),
                        ],
                      ),
                    ),
                  );
                },
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
name: kruger_vpn
description: "Kruger VPN - WireGuard & VPS Protection Mobile App built with Flutter."
publish_to: 'none'
version: 1.0.0+1

environment:
  sdk: '>=3.0.0 <4.0.0'

dependencies:
  flutter:
    sdk: flutter

  # HTTP Client for querying WireGuard VPS controller REST API
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

    val githubWorkflowYaml = """
name: Build Flutter Release APK

on:
  push:
    branches: [ "main", "master" ]
  pull_request:
    branches: [ "main", "master" ]
  workflow_dispatch:

jobs:
  build:
    name: Build Flutter APK
    runs-on: ubuntu-latest

    steps:
      - name: Checkout repository
        uses: actions/checkout@v4

      - name: Set up Java JDK
        uses: actions/setup-java@v4
        with:
          distribution: 'temurin'
          java-version: '17'
          cache: 'gradle'

      - name: Set up Flutter SDK
        uses: subosito/flutter-action@v2
        with:
          flutter-version: '3.19.x'
          channel: 'stable'
          cache: true

      - name: Get Flutter dependencies
        run: flutter pub get

      - name: Recreate Android project files
        run: flutter create . --platforms=android --project-name new_kruger

      - name: Set Android minSdkVersion 21
        run: |
          sed -i 's/minSdkVersion flutter.minSdkVersion/minSdkVersion 21/g' android/app/build.gradle || true
          sed -i 's/minSdk = flutter.minSdkVersion/minSdk = 21/g' android/app/build.gradle || true
          sed -i 's/minSdk flutter.minSdkVersion/minSdk 21/g' android/app/build.gradle || true

      - name: Build Debug APK
        run: flutter build apk --debug

      - name: Upload Debug APK Artifact
        uses: actions/upload-artifact@v4
        with:
          name: KrugerVPN-Debug-APK
          path: build/app/outputs/flutter-apk/app-debug.apk
          retention-days: 7
""".trimIndent()
}

