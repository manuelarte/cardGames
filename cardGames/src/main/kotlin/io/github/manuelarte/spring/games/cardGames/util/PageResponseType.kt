package io.github.manuelarte.spring.games.cardGames.util

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonProperty
import org.axonframework.common.ReflectionUtils
import org.axonframework.messaging.responsetypes.AbstractResponseType
import org.springframework.data.domain.Page
import java.beans.ConstructorProperties
import java.lang.reflect.Type
import java.util.concurrent.Future

class PageResponseType<R>
/**
 * Instantiate a [ResponseType] with the given `expectedResponseType` as the type to be matched against and
 * to which the query response should be converted to, as is or as the contained type for an array/list/etc.
 *
 * @param expectedResponseType the response type which is expected to be matched against and to be returned, as is or as
 * the contained type for an array/list/etc
 */
@JsonCreator @ConstructorProperties("expectedResponseType") constructor(@JsonProperty("expectedResponseType") expectedResponseType: Class<R>?) : AbstractResponseType<Page<R>?>(expectedResponseType) {
    override fun matches(responseType: Type): Boolean {
        val unwrapped = ReflectionUtils.unwrapIfType(responseType, Future::class.java, Page::class.java)
        return isGenericAssignableFrom(unwrapped) || isAssignableFrom(unwrapped)
    }

    override fun convert(response: Any): Page<R> {
        return response as Page<R>
    }

    override fun responseMessagePayloadType(): Class<Page<R>?> {
        return Page::class.java as Class<Page<R>?>
    }

    override fun toString(): String {
        return "PageResponseType{$expectedResponseType}"
    }
}