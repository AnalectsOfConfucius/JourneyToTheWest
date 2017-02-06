(function() {
    'use strict';

    angular
        .module('journeyToTheWestApp')
        .controller('LawEntryDialogController', LawEntryDialogController);

    LawEntryDialogController.$inject = ['$timeout', '$scope', '$stateParams', '$uibModalInstance', 'entity', 'LawEntry', 'Law'];

    function LawEntryDialogController ($timeout, $scope, $stateParams, $uibModalInstance, entity, LawEntry, Law) {
        var vm = this;

        vm.lawEntry = entity;
        vm.clear = clear;
        vm.save = save;
        vm.laws = Law.query();

        $timeout(function (){
            angular.element('.form-group:eq(1)>input').focus();
        });

        function clear () {
            $uibModalInstance.dismiss('cancel');
        }

        function save () {
            vm.isSaving = true;
            if (vm.lawEntry.id !== null) {
                LawEntry.update(vm.lawEntry, onSaveSuccess, onSaveError);
            } else {
                LawEntry.save(vm.lawEntry, onSaveSuccess, onSaveError);
            }
        }

        function onSaveSuccess (result) {
            $scope.$emit('journeyToTheWestApp:lawEntryUpdate', result);
            $uibModalInstance.close(result);
            vm.isSaving = false;
        }

        function onSaveError () {
            vm.isSaving = false;
        }


    }
})();
