const fs=require('fs');
const path=require('path');
const root=__dirname;
const android=path.join(root,'android');
const pkg=path.join(android,'app','src','main','java','com','niceelectrical','pro');
if(!fs.existsSync(android))throw new Error('Android platform folder not found.');
fs.mkdirSync(pkg,{recursive:true});
for(const name of ['MainActivity.java','NicePrintPlugin.java']){const from=path.join(root,'native-android',name);if(!fs.existsSync(from))throw new Error('Missing '+name);fs.copyFileSync(from,path.join(pkg,name));}
console.log('Installed native Android print bridge.');
