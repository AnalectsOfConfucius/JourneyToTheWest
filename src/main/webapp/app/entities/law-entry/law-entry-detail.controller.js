(function() {
    'use strict';

    angular
        .module('journeyToTheWestApp')
        .controller('LawEntryDetailController', LawEntryDetailController);

    LawEntryDetailController.$inject = ['$scope', '$rootScope', '$stateParams', 'previousState', 'entity', 'LawEntry', 'Law'];

    function LawEntryDetailController($scope, $rootScope, $stateParams, previousState, entity, LawEntry, Law) {
        var vm = this;

        vm.lawEntry = entity;
        vm.previousState = previousState.name;

        var unsubscribe = $rootScope.$on('journeyToTheWestApp:lawEntryUpdate', function(event, result) {
            vm.lawEntry = result;
        });
        $scope.$on('$destroy', unsubscribe);
    }
})();
