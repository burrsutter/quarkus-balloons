"use strict";
var __extends = (this && this.__extends) || function (d, b) {
    for (var p in b) if (b.hasOwnProperty(p)) d[p] = b[p];
    function __() { this.constructor = d; }
    d.prototype = b === null ? Object.create(b) : (__.prototype = b.prototype, new __());
};
var parameterDecoratorBase_1 = require('./parameterDecoratorBase');
var Rule = (function (_super) {
    __extends(Rule, _super);
    function Rule(ruleName, value, disabledIntervals) {
        _super.call(this, {
            decoratorName: 'Input',
            propertyName: 'inputs',
            errorMessage: null
        }, ruleName, value, disabledIntervals);
    }
    return Rule;
}(parameterDecoratorBase_1.UseParameterDecorator));
exports.Rule = Rule;
