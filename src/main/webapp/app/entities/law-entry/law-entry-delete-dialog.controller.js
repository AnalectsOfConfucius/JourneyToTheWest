(function() {
    'use strict';

    angular
        .module('journeyToTheWestApp')
        .controller('LawEntryDeleteController',LawEntryDeleteController);

    LawEntryDeleteController.$inject = ['$uibModalInstance', 'entity', 'LawEntry'];

    function LawEntryDeleteController($uibModalInstance, entity, LawEntry) {
        var vm = this;

        vm.lawEntry = entity;
        vm.clear = clear;
        vm.confirmDelete = confirmDelete;

        function clear () {
            $uibModalInstance.dismiss('cancel');
        }

        function confirmDelete (id) {
            LawEntry.delete({id: id},
                function () {
                    $uibModalInstance.close(true);
                });
        }
    }
})();
