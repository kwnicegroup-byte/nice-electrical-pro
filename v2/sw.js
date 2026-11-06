const CACHE='nep-v2-shell-17';
const FILES=['./','./index.html','./manifest.webmanifest'];
self.addEventListener('install',e=>e.waitUntil(caches.open(CACHE).then(c=>c.addAll(FILES)).then(()=>self.skipWaiting())));
self.addEventListener('activate',e=>e.waitUntil(caches.keys().then(keys=>Promise.all(keys.filter(k=>k.startsWith('nep-v2-')&&k!==CACHE).map(k=>caches.delete(k)))).then(()=>self.clients.claim())));
self.addEventListener('fetch',e=>{
 if(e.request.method!=='GET')return;
 const url=new URL(e.request.url);
 const isPage=e.request.mode==='navigate'||url.pathname.endsWith('/index.html')||url.pathname.endsWith('/company-settings-test.html');
 if(isPage){
  e.respondWith(fetch(e.request,{cache:'no-store'}).then(res=>{if(res.ok&&url.origin===self.location.origin){const copy=res.clone();caches.open(CACHE).then(c=>c.put(e.request,copy));}return res;}).catch(()=>caches.match(e.request).then(cached=>cached||caches.match('./index.html'))));
  return;
 }
 e.respondWith(caches.match(e.request).then(cached=>cached||fetch(e.request).then(res=>{if(res.ok&&url.origin===self.location.origin){const copy=res.clone();caches.open(CACHE).then(c=>c.put(e.request,copy));}return res;})));
});