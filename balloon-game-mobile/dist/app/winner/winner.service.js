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
var WinnerService = (function () {
    function WinnerService() {
        this.connected = false;
    }
    WinnerService.prototype.connect = function (newPlayerId) {
        var _this = this;
        if (newPlayerId && this.connected) {
            this.ws.close();
            console.log('in here');
            this.connected = false;
        }
        var playerId = localStorage.getItem('player-id');
        if (!playerId || this.connected) {
            return;
        }
        this.connected = true;
        this.ws = new WebSocket('ws://localhost:8081/game/winner');
        this.ws.onopen = function (event) {
            console.log('connected to game winner');
            _this.ws.send(playerId);
        };
        this.ws.onmessage = function (event) {
            if (!event.data) {
                return;
            }
            console.log(event);
            try {
                var data = JSON.parse(event.data);
                if (data.type === 'winner') {
                    console.log('You are a winner');
                }
            }
            catch (error) {
            }
        };
    };
    WinnerService = __decorate([
        core_1.Injectable(), 
        __metadata('design:paramtypes', [])
    ], WinnerService);
    return WinnerService;
}());
exports.WinnerService = WinnerService;
//# sourceMappingURL=winner.service.js.map