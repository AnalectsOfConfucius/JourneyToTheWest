(function() {
    'use strict';

    angular
        .module('journeyToTheWestApp')
        .config(stateConfig);

    stateConfig.$inject = ['$stateProvider'];

    function stateConfig($stateProvider) {
        $stateProvider
        .state('law-entry', {
            parent: 'entity',
            url: '/law-entry',
            data: {
                authorities: ['ROLE_USER'],
                pageTitle: 'journeyToTheWestApp.lawEntry.home.title'
            },
            views: {
                'content@': {
                    templateUrl: 'app/entities/law-entry/law-entries.html',
                    controller: 'LawEntryController',
                    controllerAs: 'vm'
                }
            },
            resolve: {
                translatePartialLoader: ['$translate', '$translatePartialLoader', function ($translate, $translatePartialLoader) {
                    $translatePartialLoader.addPart('lawEntry');
                    $translatePartialLoader.addPart('global');
                    return $translate.refresh();
                }]
            }
        })
        .state('law-entry-detail', {
            parent: 'entity',
            url: '/law-entry/{id}',
            data: {
                authorities: ['ROLE_USER'],
                pageTitle: 'journeyToTheWestApp.lawEntry.detail.title'
            },
            views: {
                'content@': {
                    templateUrl: 'app/entities/law-entry/law-entry-detail.html',
                    controller: 'LawEntryDetailController',
                    controllerAs: 'vm'
                }
            },
            resolve: {
                translatePartialLoader: ['$translate', '$translatePartialLoader', function ($translate, $translatePartialLoader) {
                    $translatePartialLoader.addPart('lawEntry');
                    return $translate.refresh();
                }],
                entity: ['$stateParams', 'LawEntry', function($stateParams, LawEntry) {
                    return LawEntry.get({id : $stateParams.id}).$promise;
                }],
                previousState: ["$state", function ($state) {
                    var currentStateData = {
                        name: $state.current.name || 'law-entry',
                        params: $state.params,
                        url: $state.href($state.current.name, $state.params)
                    };
                    return currentStateData;
                }]
            }
        })
        .state('law-entry-detail.edit', {
            parent: 'law-entry-detail',
            url: '/detail/edit',
            data: {
                authorities: ['ROLE_USER']
            },
            onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                $uibModal.open({
                    templateUrl: 'app/entities/law-entry/law-entry-dialog.html',
                    controller: 'LawEntryDialogController',
                    controllerAs: 'vm',
                    backdrop: 'static',
                    size: 'lg',
                    resolve: {
                        entity: ['LawEntry', function(LawEntry) {
                            return LawEntry.get({id : $stateParams.id}).$promise;
                        }]
                    }
                }).result.then(function() {
                    $state.go('^', {}, { reload: false });
                }, function() {
                    $state.go('^');
                });
            }]
        })
        .state('law-entry.new', {
            parent: 'law-entry',
            url: '/new',
            data: {
                authorities: ['ROLE_USER']
            },
            onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                $uibModal.open({
                    templateUrl: 'app/entities/law-entry/law-entry-dialog.html',
                    controller: 'LawEntryDialogController',
                    controllerAs: 'vm',
                    backdrop: 'static',
                    size: 'lg',
                    resolve: {
                        entity: function () {
                            return {
                                lawEntryTitle: null,
                                lawEntryContent: null,
                                id: null
                            };
                        }
                    }
                }).result.then(function() {
                    $state.go('law-entry', null, { reload: 'law-entry' });
                }, function() {
                    $state.go('law-entry');
                });
            }]
        })
        .state('law-entry.edit', {
            parent: 'law-entry',
            url: '/{id}/edit',
            data: {
                authorities: ['ROLE_USER']
            },
            onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                $uibModal.open({
                    templateUrl: 'app/entities/law-entry/law-entry-dialog.html',
                    controller: 'LawEntryDialogController',
                    controllerAs: 'vm',
                    backdrop: 'static',
                    size: 'lg',
                    resolve: {
                        entity: ['LawEntry', function(LawEntry) {
                            return LawEntry.get({id : $stateParams.id}).$promise;
                        }]
                    }
                }).result.then(function() {
                    $state.go('law-entry', null, { reload: 'law-entry' });
                }, function() {
                    $state.go('^');
                });
            }]
        })
        .state('law-entry.delete', {
            parent: 'law-entry',
            url: '/{id}/delete',
            data: {
                authorities: ['ROLE_USER']
            },
            onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                $uibModal.open({
                    templateUrl: 'app/entities/law-entry/law-entry-delete-dialog.html',
                    controller: 'LawEntryDeleteController',
                    controllerAs: 'vm',
                    size: 'md',
                    resolve: {
                        entity: ['LawEntry', function(LawEntry) {
                            return LawEntry.get({id : $stateParams.id}).$promise;
                        }]
                    }
                }).result.then(function() {
                    $state.go('law-entry', null, { reload: 'law-entry' });
                }, function() {
                    $state.go('^');
                });
            }]
        });
    }

})();
