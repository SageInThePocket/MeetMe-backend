package com.meetme.domain.service.search

import com.meetme.domain.AllEntitiesGetter
import com.meetme.domain.filter.Filter

abstract class BaseGlobalSearchService<Query, Entity>(
    private val allEntitiesGetter: AllEntitiesGetter<Entity>,
    private val filter: Filter<Entity, Query>,
) : GlobalSearchService<Query, Entity> {

    abstract fun prepareForResult(filteredEntities: List<Entity>): List<Entity>

    final override fun search(query: Query): List<Entity> {
        val allEntities = allEntitiesGetter.getAll()
        val filteredEntities = allEntities
            .filter { searchedEntity -> filter(searchedEntity, query) }
        return prepareForResult(filteredEntities)
    }
}