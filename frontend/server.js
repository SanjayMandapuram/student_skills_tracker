const http = require('http'), fs = require('fs'), path = require('path');
const root = __dirname, port = Number(process.env.PORT || 5500), backend = new URL(process.env.BACKEND_URL || 'http://127.0.0.1:8080');
const types = { '.html': 'text/html; charset=utf-8', '.js': 'text/javascript; charset=utf-8', '.css': 'text/css; charset=utf-8' };
function proxy(req, res) {
  const request = http.request({ hostname: backend.hostname, port: backend.port, path: req.url, method: req.method, headers: { ...req.headers, host: backend.host } }, response => {
    res.writeHead(response.statusCode, response.headers); response.pipe(res);
  });
  request.on('error', () => { if (!res.headersSent) res.writeHead(502, { 'Content-Type': 'application/json' }); res.end(JSON.stringify({ message: 'Backend is unavailable.' })); });
  req.pipe(request);
}
http.createServer((req, res) => {
  if (req.url.startsWith('/api/')) return proxy(req, res);
  let name = decodeURIComponent(req.url.split('?')[0]); if (name === '/' || name === '') name = '/index.html';
  const file = path.resolve(root, '.' + name);
  if (!file.startsWith(root + path.sep) || !fs.existsSync(file) || fs.statSync(file).isDirectory()) { res.writeHead(404); return res.end('Not found'); }
  res.writeHead(200, { 'Content-Type': types[path.extname(file)] || 'application/octet-stream', 'Cache-Control': 'no-store' }); fs.createReadStream(file).pipe(res);
}).listen(port, () => console.log(`Frontend running at http://localhost:${port}`));
