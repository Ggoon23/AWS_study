import uvicorn
import argparse
from app.config import settings

def main():
    parser = argparse.ArgumentParser(description='Digital Asset Management API Server')
    parser.add_argument('--host', default='0.0.0.0', help='Host to bind')
    parser.add_argument('--port', type=int, default=8000, help='Port to bind')
    parser.add_argument('--reload', action='store_true', help='Enable auto-reload')
    parser.add_argument('--workers', type=int, default=1, help='Number of worker processes')
    
    args = parser.parse_args()
    
    print(f"""
🚀 Starting Digital Asset Management API Server
------------------------------------------------
📡 Host: {args.host}
🔌 Port: {args.port}
🔄 Reload: {args.reload}
👥 Workers: {args.workers}
------------------------------------------------
    """)
    
    uvicorn.run(
        "app.main:app",
        host=args.host,
        port=args.port,
        reload=args.reload,
        workers=args.workers
    )

if __name__ == "__main__":
    main() 