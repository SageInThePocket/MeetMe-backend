package com.meetme

import com.meetme.data.DataResponse
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
inline fun <reified T1, reified T2, M> Pair<Long, Long>.doIfExist(
    dao1: JpaRepository<T1, Long>,
    dao2: JpaRepository<T2, Long>,
    logger: Logger, action: (T1, T2) -> M): M
{
    val entity1 = this.first.getEntity(dao1, logger)
    val entity2 = this.second.getEntity(dao2, logger)
    if (entity1 != null && entity2 != null)
        return action(entity1, entity2)
    else if (entity1 == null)
        throw NoSuchElementException("${T1::class.java.simpleName} with id = $this not found")
    else
        throw NoSuchElementException("${T2::class.java.simpleName} with id = $this not found")
}

inline fun <T> tryExecute(action: () -> T): DataResponse<T> {
    return try {
        DataResponse(data = action())
    } catch (e: NoSuchElementException) {
        DataResponse(message = e.message ?: "Failed to complete request")
    }
}