var sys = require('util'),
    fs = require('fs'),
    childProcess = require('child_process');

var ThumbsGenerator = function () {

    this.generateThumbs = function (source, target, callback) {
        if (source && target) {
          this.options = {
              platform: null,
              command: '',
              bin: 'bin',
              tmp: ''
          };
          if (process.platform === 'linux') {
              this.options.platform = 'linux';
              this.options.command = 'java -cp bin/ThumbsGenerator.jar';
              this.options.tmp = '/tmp';
          } else { // windows
              this.options.platform = 'win';
              this.options.command = 'java -cp bin\\ThumbsGenerator.jar';
              this.options.tmp = 'C:\\tmp';
          }
//        command = 'java -version';
            exec_child_process(this.options.command + ' "' + source + '" "' + target + '"', {
                stdout: callback,
                stderr: function (stderr) { sys.log('Child Process STDERR: '+stderr); }
            });
        }
    };

};

exports.ThumbsGenerator = ThumbsGenerator;

var exec_child_process = function (command, callbacks) {
    var child_ps;
    if (command) {
        child_ps = childProcess.exec(command, function (error, stdout, stderr) {
            if (error) {
                sys.log(error.stack);
                sys.log('Error code: '+error.code);
                sys.log('Signal received: '+error.signal);
            }
            if (stdout) {
//                sys.log('Child Process STDOUT: '+stdout);
                if (callbacks.stdout) {
                    callbacks.stdout(stdout);
                }
            }
            if (stderr) {
//                sys.log('Child Process STDERR: '+stderr);
                if (callbacks.stderr) {
                    callbacks.stderr(stderr);
                }
            }
        });
        child_ps.on('exit', function (code) {
//            sys.log('Child process exited with exit code '+code);
            if (callbacks.exit) {
                callbacks.exit(code);
            }
        });
    }
};

function dirExists (d, cb) {
    fs.stat(d, function (er, s) { cb(!er) })
}