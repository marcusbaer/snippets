var argv = require('optimist').argv,
    fs = require('fs'),
    http = require('http'),
    https = require('https'),
    httpProxy = require('http-proxy');

var options = {
    https: {
        key: fs.readFileSync('./ssl/localhost.key', 'utf8'),
        cert: fs.readFileSync('./ssl/localhost.cert', 'utf8')
    }
};

console.log("Starting PROXY SERVERS...\n\nStart with parameter --target=80 to proxy to a different port than 9090\nUse parameter --https=443 to configure a different https port than 443\nUse parameter --http=80 to configure a different http port than 80\n");

var httpPort = argv.http || 80;
var httpsPort = argv.https || 443;
var targetPort = argv.target || 9090;
var targetHost = 'localhost';

//
// Create a standalone HTTPS proxy server
//
httpProxy.createServer(targetPort, targetHost, options).listen(httpsPort);
console.log("OK\tHTTPS proxy is directing https://" + targetHost + ':' + httpsPort + ' to http://' + targetHost + ':' + targetPort);

process.on('uncaughtException', function(e){
    if (e.code === 'EADDRINUSE') {
        console.log("ERR\tHTTP proxy not started! Port " + httpPort + ' already in use! Trying to start another HTTPS proxy to that port instead..');
        //
        // Create an instance of HttpProxy to use with another HTTPS server that directs to MOCK SERVER on port 80
        //
        var proxy = new httpProxy.HttpProxy({
            target: {
                host: targetHost,
                port: httpPort
            }
        });
        https.createServer(options.https, function (req, res) {
            proxy.proxyRequest(req, res)
        }).listen(httpsPort + 1);
        console.log("OK\tSecond HTTPS proxy is directing https://" + targetHost + ':' + (httpsPort+1) + ' to http://' + targetHost + ':' + httpPort);
    } else {
        console.log(e);
    }
});

httpProxy.createServer(targetPort, 'localhost').listen(httpPort, function(){
    console.log("OK\tHTTP proxy is directing http://" + targetHost + ':' + httpPort + ' to http://' + targetHost + ':' + targetPort);
});

