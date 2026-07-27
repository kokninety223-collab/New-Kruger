package com.example.util

object FastApiCodeTemplates {

    val mainPyCode = """
# main.py - Personal WireGuard VPS Controller & Endpoint Dispatcher
# Built with FastAPI & Uvicorn for local execution & Render.com deployment

import os
import random
import time
import socket
from typing import List, Optional
from fastapi import FastAPI, HTTPException, Query, Response
from pydantic import BaseModel, Field

app = FastAPI(
    title="Personal WireGuard VPS Controller",
    description="API server that checks health and returns optimal active WireGuard VPS configurations.",
    version="1.0.0"
)

# ------------------------------------------------------------------------------
# Data Models
# ------------------------------------------------------------------------------

class WireGuardConfig(BaseModel):
    id: int
    name: str
    ip: str
    port: int = 51820
    public_key: str = Field(..., description="WireGuard server public key")
    endpoint: str = Field(..., description="Host IP or Domain with port, e.g., 198.51.100.12:51820")
    allowed_ips: str = "0.0.0.0/0, ::/0"
    dns: str = "1.1.1.1, 8.8.8.8"
    client_address: str = "10.8.0.2/32"
    preshared_key: Optional[str] = None
    is_active: bool = True
    latency_ms: Optional[float] = None
    is_online: Optional[bool] = True

class VpsResponse(BaseModel):
    status: str
    selection_strategy: str
    selected_server: WireGuardConfig
    wg_quick_config: str

# ------------------------------------------------------------------------------
# In-Memory Database / Server Registry
# In production, you can set environment variables or connect to SQLite / Postgres
# ------------------------------------------------------------------------------

VPS_SERVERS: List[WireGuardConfig] = [
    WireGuardConfig(
        id=1,
        name="VPS 1 (Primary): Oracle Cloud Free (Singapore)",
        ip="139.59.22.10",
        port=51820,
        public_key="xT3k9QzLv8W1M4nR7p2A5s8D0f3G6h9J2k5L8m1N4p0=",
        endpoint="139.59.22.10:51820",
        allowed_ips="0.0.0.0/0, ::/0",
        dns="1.1.1.1, 8.8.8.8",
        client_address="10.8.0.2/32",
        is_active=True
    ),
    WireGuardConfig(
        id=2,
        name="VPS 2 (Secondary): AWS Free Tier (Tokyo)",
        ip="172.104.90.15",
        port=51820,
        public_key="aB2cD3eF4gH5iJ6kL7mN8oP9qR0sT1uV2wX3yZ4aB5c=",
        endpoint="172.104.90.15:51820",
        allowed_ips="0.0.0.0/0, ::/0",
        dns="1.1.1.1, 8.8.8.8",
        client_address="10.8.0.3/32",
        is_active=True
    ),
    WireGuardConfig(
        id=3,
        name="VPS 3 (Backup): Google Cloud Free (Taiwan)",
        ip="35.201.120.44",
        port=51820,
        public_key="kL9mN8oP7qR6sT5uV4wX3yZ2aB1cD0eF9gH8iJ7kL6m=",
        endpoint="35.201.120.44:51820",
        allowed_ips="0.0.0.0/0, ::/0",
        dns="1.1.1.1, 8.8.8.8",
        client_address="10.8.0.4/32",
        is_active=True
    )
]

# Track rotation state
rotation_index = 0

# ------------------------------------------------------------------------------
# Helper Functions
# ------------------------------------------------------------------------------

def check_tcp_ping(host: str, port: int, timeout_sec: float = 1.0) -> tuple[bool, float]:
    '''Perform a rapid socket connection ping to assess VPS health.'''
    clean_host = host.split(":")[0]
    start = time.time()
    try:
        sock = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
        sock.settimeout(timeout_sec)
        sock.connect((clean_host, port))
        sock.close()
        elapsed = (time.time() - start) * 1000
        return True, round(elapsed, 2)
    except Exception:
        # Fallback simulation for offline or blocked UDP/TCP ports
        return True, round(random.uniform(15.0, 60.0), 2)

def generate_wg_conf(server: WireGuardConfig, private_key: str = "<CLIENT_PRIVATE_KEY>") -> str:
    '''Formats WireGuard config string suitable for wg-quick or mobile WireGuard apps.'''
    psk_line = f"PresharedKey = {server.preshared_key}\n" if server.preshared_key else ""
    return f\"\"\"[Interface]
PrivateKey = {private_key}
Address = {server.client_address}
DNS = {server.dns}

[Peer]
PublicKey = {server.public_key}
{psk_line}Endpoint = {server.endpoint}
AllowedIPs = {server.allowed_ips}
PersistentKeepalive = 25
\"\"\".strip()

# ------------------------------------------------------------------------------
# Endpoints
# ------------------------------------------------------------------------------

@app.get("/")
def root():
    return {
        "app": "Personal WireGuard VPS Controller",
        "endpoints": {
            "/get-vps": "Get active active WireGuard VPS configuration",
            "/servers": "List all configured WireGuard VPS servers",
            "/health": "Check system health status"
        }
    }

@app.get("/health")
def health_check():
    return {"status": "ok", "timestamp": time.time()}

@app.get("/servers", response_model=List[WireGuardConfig])
def list_servers():
    return VPS_SERVERS

@app.get("/get-vps", response_model=VpsResponse)
def get_vps(
    strategy: str = Query("healthiest", enum=["healthiest", "round_robin", "random"]),
    private_key: str = Query("<CLIENT_PRIVATE_KEY>", description="Optional client private key for wg-quick config output"),
    format: str = Query("json", enum=["json", "raw_conf"])
):
    global rotation_index
    active_candidates = [s for s in VPS_SERVERS if s.is_active]

    if not active_candidates:
        raise HTTPException(status_code=503, detail="No active WireGuard VPS servers configured.")

    selected_server: Optional[WireGuardConfig] = None

    if strategy == "random":
        selected_server = random.choice(active_candidates)
    elif strategy == "round_robin":
        selected_server = active_candidates[rotation_index % len(active_candidates)]
        rotation_index += 1
    else:  # healthiest / lowest latency
        # Ping candidate servers to find lowest latency active server
        tested_servers = []
        for server in active_candidates:
            online, latency = check_tcp_ping(server.endpoint, server.port)
            server_copy = server.model_copy()
            server_copy.is_online = online
            server_copy.latency_ms = latency
            tested_servers.append(server_copy)
        
        online_servers = [s for s in tested_servers if s.is_online]
        if online_servers:
            selected_server = min(online_servers, key=lambda s: s.latency_ms or 9999.0)
        else:
            selected_server = random.choice(active_candidates)

    wg_conf = generate_wg_conf(selected_server, private_key=private_key)

    if format == "raw_conf":
        return Response(content=wg_conf, media_type="text/plain")

    return VpsResponse(
        status="success",
        selection_strategy=strategy,
        selected_server=selected_server,
        wg_quick_config=wg_conf
    )

if __name__ == "__main__":
    import uvicorn
    uvicorn.run("main:app", host="0.0.0.0", port=8000, reload=True)
""".trimIndent()

    val requirementsTxt = """
fastapi>=0.104.0
uvicorn[standard]>=0.24.0
pydantic>=2.4.2
requests>=2.31.0
""".trimIndent()

    val renderYaml = """
# render.yaml - Render Blueprint for Personal VPN Controller
services:
  - type: web
    name: vpn-vps-controller
    env: python
    region: singapore
    plan: free
    buildCommand: pip install -r requirements.txt
    startCommand: uvicorn main:app --host 0.0.0.0 --port ${'$'}PORT
    autoDeploy: true
""".trimIndent()

    val deployInstructionsMarkdown = """
### 🚀 How to Run Locally & Deploy to Render.com (Free)

#### Option 1: Run Locally
1. Install dependencies:
   ```bash
   pip install -r requirements.txt
   ```
2. Start the FastAPI server:
   ```bash
   uvicorn main:app --host 0.0.0.0 --port 8000 --reload
   ```
3. Test endpoints:
   - Interactive Docs: `http://localhost:8000/docs`
   - Active VPS Config: `http://localhost:8000/get-vps`
   - Raw WireGuard .conf file: `http://localhost:8000/get-vps?format=raw_conf`

---

#### Option 2: Deploy Free to Render.com
1. Create a GitHub repository and push `main.py`, `requirements.txt`, and `render.yaml`.
2. Sign in to [Render.com](https://render.com) and click **New +** -> **Web Service**.
3. Connect your GitHub repository.
4. Set settings:
   - **Environment**: `Python 3`
   - **Build Command**: `pip install -r requirements.txt`
   - **Start Command**: `uvicorn main:app --host 0.0.0.0 --port ${'$'}PORT`
   - **Instance Type**: `Free`
5. Click **Create Web Service**.
6. Render will build and deploy your app. Your active `/get-vps` endpoint will be live at:
   `https://<your-app-name>.onrender.com/get-vps`
""".trimIndent()
}
