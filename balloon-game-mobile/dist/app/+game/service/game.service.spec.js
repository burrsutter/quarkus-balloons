"use strict";
var testing_1 = require('@angular/core/testing');
var game_service_1 = require('./game.service');
testing_1.describe('Game Service', function () {
    testing_1.beforeEachProviders(function () { return [game_service_1.GameService]; });
    testing_1.it('should ...', testing_1.inject([game_service_1.GameService], function (service) {
        testing_1.expect(service).toBeTruthy();
    }));
});
//# sourceMappingURL=game.service.spec.js.map