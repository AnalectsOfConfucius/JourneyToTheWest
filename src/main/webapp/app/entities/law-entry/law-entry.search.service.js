(function() {
    'use strict';

    angular
        .module('journeyToTheWestApp')
        .factory('LawEntrySearch', LawEntrySearch);

    LawEntrySearch.$inject = ['$resource'];

    function LawEntrySearch($resource) {
        var resourceUrl =  'api/_search/law-entries/:id';

        return $resource(resourceUrl, {}, {
            'query': { method: 'GET', isArray: true}
        });
    }
})();
