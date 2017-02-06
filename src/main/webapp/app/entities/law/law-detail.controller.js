(function() {
    'use strict';

    angular
        .module('journeyToTheWestApp')
        .controller('LawDetailController', LawDetailController);

    LawDetailController.$inject = ['$scope', '$rootScope', '$stateParams', 'previousState', 'entity', 'Law', 'LawEntry'];

    function LawDetailController($scope, $rootScope, $stateParams, previousState, entity, Law, LawEntry) {
        var vm = this;

        vm.law = entity;
        vm.previousState = previousState.name;

        var unsubscribe = $rootScope.$on('journeyToTheWestApp:lawUpdate', function(event, result) {
            vm.law = result;
        });
        $scope.$on('$destroy', unsubscribe);
    }
})();
