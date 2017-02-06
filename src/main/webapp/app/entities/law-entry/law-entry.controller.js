(function() {
    'use strict';

    angular
        .module('journeyToTheWestApp')
        .controller('LawEntryController', LawEntryController);

    LawEntryController.$inject = ['$scope', '$state', 'LawEntry', 'LawEntrySearch'];

    function LawEntryController ($scope, $state, LawEntry, LawEntrySearch) {
        var vm = this;

        vm.lawEntries = [];
        vm.clear = clear;
        vm.search = search;
        vm.loadAll = loadAll;

        loadAll();

        function loadAll() {
            LawEntry.query(function(result) {
                vm.lawEntries = result;
                vm.searchQuery = null;
            });
        }

        function search() {
            if (!vm.searchQuery) {
                return vm.loadAll();
            }
            LawEntrySearch.query({query: vm.searchQuery}, function(result) {
                vm.lawEntries = result;
                vm.currentSearch = vm.searchQuery;
            });
        }

        function clear() {
            vm.searchQuery = null;
            loadAll();
        }    }
})();
