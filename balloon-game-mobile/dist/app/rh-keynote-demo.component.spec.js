"use strict";
var testing_1 = require('@angular/core/testing');
var rh_keynote_demo_component_1 = require('../app/rh-keynote-demo.component');
testing_1.beforeEachProviders(function () { return [rh_keynote_demo_component_1.RhKeynoteDemoAppComponent]; });
testing_1.describe('App: RhKeynoteDemo', function () {
    testing_1.it('should create the app', testing_1.inject([rh_keynote_demo_component_1.RhKeynoteDemoAppComponent], function (app) {
        testing_1.expect(app).toBeTruthy();
    }));
    testing_1.it('should have as title \'rh-keynote-demo works!\'', testing_1.inject([rh_keynote_demo_component_1.RhKeynoteDemoAppComponent], function (app) {
        testing_1.expect(app.title).toEqual('rh-keynote-demo works!');
    }));
});
//# sourceMappingURL=rh-keynote-demo.component.spec.js.map