(function() {
    'use strict';
    angular
        .module('journeyToTheWestApp')
        .factory('LawEntry', LawEntry);

    LawEntry.$inject = ['$resource'];

    function LawEntry ($resource) {
        var resourceUrl =  'api/law-entries/:id';

        return $resource(resourceUrl, {}, {
            'query': { method: 'GET', isArray: true},
            'get': {
                method: 'GET',
                transformResponse: function (data) {
                    if (data) {
                        data = angular.fromJson(data);
                    }
                    return data;
                }
            },
            'update': { method:'PUT' }
        });
    }
})();
