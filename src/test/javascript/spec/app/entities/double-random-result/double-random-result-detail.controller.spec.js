'use strict';

describe('Controller Tests', function() {

    describe('DoubleRandomResult Management Detail Controller', function() {
        var $scope, $rootScope;
        var MockEntity, MockPreviousState, MockDoubleRandomResult, MockLawenforcement, MockManager, MockSign, MockCompany, MockDoubleRandom;
        var createController;

        beforeEach(inject(function($injector) {
            $rootScope = $injector.get('$rootScope');
            $scope = $rootScope.$new();
            MockEntity = jasmine.createSpy('MockEntity');
            MockPreviousState = jasmine.createSpy('MockPreviousState');
            MockDoubleRandomResult = jasmine.createSpy('MockDoubleRandomResult');
            MockLawenforcement = jasmine.createSpy('MockLawenforcement');
            MockManager = jasmine.createSpy('MockManager');
            MockSign = jasmine.createSpy('MockSign');
            MockCompany = jasmine.createSpy('MockCompany');
            MockDoubleRandom = jasmine.createSpy('MockDoubleRandom');
            

            var locals = {
                '$scope': $scope,
                '$rootScope': $rootScope,
                'entity': MockEntity,
                'previousState': MockPreviousState,
                'DoubleRandomResult': MockDoubleRandomResult,
                'Lawenforcement': MockLawenforcement,
                'Manager': MockManager,
                'Sign': MockSign,
                'Company': MockCompany,
                'DoubleRandom': MockDoubleRandom
            };
            createController = function() {
                $injector.get('$controller')("DoubleRandomResultDetailController", locals);
            };
        }));


        describe('Root Scope Listening', function() {
            it('Unregisters root scope listener upon scope destruction', function() {
                var eventType = 'journeyToTheWestApp:doubleRandomResultUpdate';

                createController();
                expect($rootScope.$$listenerCount[eventType]).toEqual(1);

                $scope.$destroy();
                expect($rootScope.$$listenerCount[eventType]).toBeUndefined();
            });
        });
    });

});
