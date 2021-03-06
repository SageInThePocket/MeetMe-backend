package com.meetme.util

import com.meetme.domain.EntityGetter
import com.meetme.domain.dto.DataResponse
import org.slf4j.Logger
import org.springframework.data.jpa.repository.JpaRepository

inline fun <reified T> Long.getEntity(dao: JpaRepository<T, Long>, logger: Logger): T? {
    val userOpt = dao.findById(this)
    return if (userOpt.isPresent) {
        val dbUser = userOpt.get()
        logger.debug("${T::class.java.simpleName} $dbUser found by id: $this")
        dbUser
    } else {
        logger.debug("${T::class.java.simpleName} not found by id: $this")
        return null
    }
}

@Throws(NoSuchElementException::class)
inline fun <reified T, M> Long.doIfExist(dao: JpaRepository<T, Long>, logger: Logger, action: (T) -> M): M {
    val entity = this.getEntity(dao, logger)
    if (entity != null)
        return action(entity)
    else
        throw NoSuchElementException("${T::class.java.simpleName} with id = $this not found")
}

@Throws(NoSuchElementException::class)
inline fun <reified T, I, M> I.doIfExist(entityGetter: EntityGetter<I, T>, action: (T) -> M): M {
    val entity = entityGetter.get(this)
    return action(entity)
}

@Throws(NoSuchElementException::class)
inline fun <reified T1, reified T2, I1, I2, M> Pair<I1, I2>.doIfExist(
    entityGetter1: EntityGetter<I1, T1>,
    entityGetter2: EntityGetter<I2, T2>,
    action: (T1, T2) -> M
): M {
    val entity1 = entityGetter1.get(this.first)
    val entity2 = entityGetter2.get(this.second)
    return action(entity1, entity2)
}

@Throws(NoSuchElementException::class)
inline fun <reified T, I, M> Pair<I, I>.doIfExist(
    entityGetter: EntityGetter<I, T>,
    action: (T, T) -> M
): M {
    val entity1 = entityGetter.get(this.first)
    val entity2 = entityGetter.get(this.second)
    return action(entity1, entity2)
}

@Throws(NoSuchElementException::class)
inline fun <reified T, M> Pair<Long, Long>.doIfExist(
    dao: JpaRepository<T, Long>,
    logger: Logger, action: (T, T) -> M
): M = this.doIfExist(dao, dao, logger, action)

@Throws(NoSuchElementException::class)
inline fun <reified T1, reified T2, M> Pair<Long, Long>.doIfExist(
    dao1: JpaRepository<T1, Long>,
    dao2: JpaRepository<T2, Long>,
    logger: Logger, action: (T1, T2) -> M
): M {
    val entity1 = this.first.getEntity(dao1, logger)
    val entity2 = this.second.getEntity(dao2, logger)
    if (entity1 != null && entity2 != null)
        return action(entity1, entity2)
    else if (entity1 == null)
        throw NoSuchElementException("${T1::class.java.simpleName} with id = ${this.first} not found")
    else
        throw NoSuchElementException("${T2::class.java.simpleName} with id = ${this.second} not found")
}

inline fun <T> tryExecute(action: () -> T): DataResponse<T> {
    return try {
        DataResponse(data = action())
    } catch (e: Exception) {
        DataResponse(message = e.message ?: "Failed to complete request: exception $e\nstack trace: ${e.stackTrace}")
    }
}

inline fun <reified T, M> List<Long>.doIfExist(
    dao: JpaRepository<T, Long>,
    logger: Logger,
    action: (T) -> M
) {
    var exceptionMessage = ""
    forEach { id ->
        val entity = id.getEntity(dao, logger)
        if (entity != null)
            action(entity)
        else
            exceptionMessage += "${T::class.java.simpleName} with id = $id not found\n"
    }
    if (exceptionMessage.isNotBlank())
        throw NoSuchElementException(exceptionMessage.trim())
}

inline fun <reified T1, reified T2, M> Pair<Long, List<Long>>.doIfExist(
    dao1: JpaRepository<T1, Long>,
    dao2: JpaRepository<T2, Long>,
    logger: Logger,
    action: (T1, T2) -> M
) {
    var exceptionMessage = ""
    val entity1 = this.first.getEntity(dao1, logger)
        ?: throw NoSuchElementException("${T1::class.java.simpleName} with id = ${this.first} not found")
    this.second.forEach { id ->
        val entity2 = id.getEntity(dao2, logger)
        if (entity2 != null)
            action(entity1, entity2)
        else
            exceptionMessage += "${T2::class.java.simpleName} with id = $id not found\n"
    }
    if (exceptionMessage.isNotBlank())
        throw NoSuchElementException(exceptionMessage.trim())
}
