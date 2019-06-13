"use strict";
var __decorate = (this && this.__decorate) || function (decorators, target, key, desc) {
    var c = arguments.length, r = c < 3 ? target : desc === null ? desc = Object.getOwnPropertyDescriptor(target, key) : desc, d;
    if (typeof Reflect === "object" && typeof Reflect.decorate === "function") r = Reflect.decorate(decorators, target, key, desc);
    else for (var i = decorators.length - 1; i >= 0; i--) if (d = decorators[i]) r = (c < 3 ? d(r) : c > 3 ? d(target, key, r) : d(target, key)) || r;
    return c > 3 && r && Object.defineProperty(target, key, r), r;
};
var __metadata = (this && this.__metadata) || function (k, v) {
    if (typeof Reflect === "object" && typeof Reflect.metadata === "function") return Reflect.metadata(k, v);
};
var core_1 = require('@angular/core');
var http_1 = require('@angular/http');
var game_service_1 = require('../+game/service/game.service');
var environment_1 = require('../environment');
var SelfieComponent = (function () {
    function SelfieComponent(http, gameService, elementRef) {
        this.http = http;
        this.gameService = gameService;
        this.elementRef = elementRef;
        this.imageUploaded = false;
        this.error = false;
        this.uploading = false;
        this.canUpload = false;
        this.scoreIncrement = 500;
        this.pointsAwarded = false;
        this.uploadUrl = (environment_1.environment.production) ? 'http://player-id-server-demo.apps-test.redhatkeynote.com/upload' : 'http://localhost:8085/upload';
    }
    SelfieComponent.prototype.onChange = function (event) {
        var _this = this;
        var self = this;
        if (event.target.files.length === 1 && event.target.files[0].type.indexOf('image/') === 0) {
            var file_1 = event.target.files[0];
            var options_1 = {
                maxWidth: 300,
                maxHeight: 300,
                crop: true,
                downsamplingRatio: 0.05
            };
            self.canUpload = true;
            loadImage.parseMetaData(file_1, function (data) {
                if (data.exif) {
                    options_1['orientation'] = data.exif.get('Orientation');
                }
                loadImage(file_1, function (data) {
                    self.canvas = document.getElementById('canvas');
                    self.canvas.parentNode.replaceChild(data, _this.canvas);
                    self.canvas = data;
                }, options_1);
            });
        }
    };
    SelfieComponent.prototype.upload = function () {
        var _this = this;
        if (this.gameService.currentState === 'game-over') {
            return;
        }
        var image = this.canvas.toDataURL('image/jpeg', 0.1);
        var body = JSON.stringify({
            image: image,
            id: localStorage.getItem('id') || ''
        });
        var headers = new http_1.Headers({ 'Content-Type': 'application/json' });
        var options = new http_1.RequestOptions({ headers: headers });
        this.pointsAwarded = false;
        this.uploading = true;
        setTimeout(function () {
            componentHandler.upgradeDom();
        }, 0);
        this.http.post(this.uploadUrl, body, options)
            .toPromise()
            .then(function (res) {
            var data = res.json();
            _this.uploading = false;
            if (data.success) {
                _this.imageUploaded = true;
                localStorage.setItem('id', data.id);
                if (!_this.gameService.selfieTaken) {
                    // give the user some points for uploading their player id
                    _this.gameService.incrementPlayerScore(_this.scoreIncrement);
                    _this.gameService.setSelfieTaken(true);
                    _this.pointsAwarded = true;
                }
            }
        })
            .catch(function (err) {
            _this.uploading = false;
            _this.imageUploaded = true;
            _this.error = true;
        });
    };
    SelfieComponent.prototype.tryAgain = function () {
        this.canUpload = false;
        this.imageUploaded = false;
        this.error = false;
        this.uploading = false;
    };
    SelfieComponent = __decorate([
        core_1.Component({
            moduleId: module.id,
            selector: 'app-selfie',
            host: { 'class': 'dark-bg' },
            templateUrl: 'selfie.component.html',
            styleUrls: ['selfie.component.css'],
            providers: [http_1.HTTP_PROVIDERS]
        }), 
        __metadata('design:paramtypes', [http_1.Http, game_service_1.GameService, core_1.ElementRef])
    ], SelfieComponent);
    return SelfieComponent;
}());
exports.SelfieComponent = SelfieComponent;
//# sourceMappingURL=selfie.component.js.map