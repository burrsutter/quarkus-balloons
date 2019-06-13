"use strict";
var testing_1 = require('@angular/core/testing');
var winner_service_1 = require('./winner.service');
testing_1.describe('Winner Service', function () {
    testing_1.beforeEachProviders(function () { return [winner_service_1.WinnerService]; });
    testing_1.it('should ...', testing_1.inject([winner_service_1.WinnerService], function (service) {
        testing_1.expect(service).toBeTruthy();
    }));
});
//# sourceMappingURL=winner.service.spec.js.map