package ds.ankobindings

import android.view.View
import java.util.*
import kotlin.jvm.internal.CallableReference
import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KClass
import kotlin.reflect.KProperty
import kotlin.reflect.KProperty0

private val bindings = WeakHashMap<ViewModel, MutableMap<String, BindingData<*, *>>>()

inline fun <reified T : Any> binding(initialValue: T? = null): ReadWriteProperty<ViewModel, T> = BindingProperty(initialValue, T::class)

class BindingProperty<T : Any>(var value: T?, val type: KClass<T>) : ReadWriteProperty<ViewModel, T> {

    override fun getValue(thisRef: ViewModel, property: KProperty<*>): T {
        val b = getBinding<T>(thisRef, property)?.getter
        return b?.invoke() ?: value ?: default(type)
    }

    override fun setValue(thisRef: ViewModel, property: KProperty<*>, value: T) {
        val oldValue = this.value
        if (oldValue != value) {
            this.value = value
            getBinding<T>(thisRef, property)?.setters?.forEach { it(value) }
        }
    }

    @Suppress("UNCHECKED_CAST")
    fun default(cls: KClass<T>): T {
        return when (cls) {
            String::class -> "" as T
            CharSequence::class -> "" as T
            java.lang.Integer::class -> 0 as T
            java.lang.Boolean::class -> false as T
            java.lang.Float::class -> 0f as T
            java.lang.Double::class -> 0.0 as T
            else -> cls.java.newInstance()
        }
    }

}

@Suppress("UNCHECKED_CAST", "NOTHING_TO_INLINE")
private inline fun <T> getBinding(vm: ViewModel, prop: KProperty<*>): BindingData<T, T>? =
    bindings.getOrPut(vm, { mutableMapOf<String, BindingData<*, *>>() })[prop.name] as BindingData<T, T>?


// alternative bind
@Suppress("unused")
fun <T : Any?> View.bind(prop: KProperty0<T>, setter: (T) -> Unit, getter: (() -> T)? = null) {
    bindInternal(prop, setter, getter)
}

private fun <T : Any?> bindInternal(prop: KProperty0<T>, setter: (T) -> Unit, getter: (() -> T)? = null) {
    val owner: ViewModel = (prop as CallableReference).boundReceiver as ViewModel
    println("bind ${prop.name}")
    val binding = getBinding<T>(owner, prop) ?: BindingData()
    binding.setters += setter
    binding.field = prop.name
    if (getter != null)
        if (binding.getter == null)
            binding.getter = getter
        else
            error("Only one getter per property allowed")

    setter(prop.get())  // initialize view
    bindings[owner]!!.put(prop.name, binding)
}

operator fun <T : ViewModel, P> T.invoke(block: (T) -> KProperty0<P>): Pair<T, KProperty0<P>> = Pair(this, block(this))

fun <T : Any?> ViewModel.unbind(prop: KProperty<T>) {
    bindings[this]?.remove(prop.name)
}

fun ViewModel.unbindAll() {
    bindings.remove(this)
}

fun ViewModel.debugBindings() {
    bindings[this]?.forEach { k, v ->
        println("for ${v.field}: id=$k getter=${v.getter} setters=${v.setters.size}")
    }
}


private class BindingData<T : Any?, R : Any?> {
    var field: String = ""
    var getter: (() -> R)? = null
    val setters = mutableListOf<(T) -> Unit>()
}

interface ViewModel

