const fs = require('fs');
const path = require('path');
const source = path.resolve(__dirname, '..');
const target = path.resolve(__dirname, 'www');
fs.rmSync(target, { recursive: true, force: true });
fs.mkdirSync(target, { recursive: true });
for (const name of ['index.html', 'manifest.webmanifest', 'sw.js']) {
  const from = path.join(source, name);
  if (!fs.existsSync(from)) throw new Error('Missing V2 web file: ' + name);
  fs.copyFileSync(from, path.join(target, name));
}
console.log('Prepared V2 web app for Android build.');
