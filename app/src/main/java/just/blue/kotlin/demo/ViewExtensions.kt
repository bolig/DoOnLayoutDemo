package just.blue.kotlin.demo

import android.os.Build
import android.support.v4.view.ViewCompat
import android.view.View
import android.view.ViewTreeObserver

/** Plan1 */

/**
 * Conditional [ViewTreeObserver.removeOnGlobalLayoutListener]
 *
 * @param runLayout run Layout
 * @param removePredicate return true, remove callback; Otherwise, no operation
 */
inline fun View.doOnLayout(crossinline removePredicate: () -> Boolean = { true }, crossinline runLayout: () -> Unit) {
    if (ViewCompat.isLaidOut(this) && !isLayoutRequested) {
        kotlin.run(runLayout)

        if (kotlin.run(removePredicate)) {
            return
        }
    }

    val vto = viewTreeObserver
    vto.addOnGlobalLayoutListener(object : ViewTreeObserver.OnGlobalLayoutListener {
        override fun onGlobalLayout() {
            kotlin.run(runLayout)

            if (kotlin.run(removePredicate)) {
                when {
                    vto.isAlive -> removeOnGlobalLayoutListener(vto, this)
                    else -> removeOnGlobalLayoutListener(viewTreeObserver, this)
                }
            }
        }
    })
}

inline fun removeOnGlobalLayoutListener(
        viewTreeObserver: ViewTreeObserver,
        listener: ViewTreeObserver.OnGlobalLayoutListener) {

    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
        viewTreeObserver.removeOnGlobalLayoutListener(listener)
    } else {
        viewTreeObserver.removeGlobalOnLayoutListener(listener)
    }
}

/** Plan2 */

/** one (removeCallback, actualValue) model */
data class RemoveAndValue<T>(val removeCallback: Boolean, val actualValue: T)

/** create RemoveAndValue model when Method does not require a return value. */
inline fun Boolean.removeAndUnit(): RemoveAndValue<Unit> = RemoveAndValue(this, Unit)

/** create RemoveAndValue model when Method requires a return value. */
inline infix fun <T> Boolean.removeAndReturn(actualValue: T): RemoveAndValue<T> =
        RemoveAndValue(this, actualValue)

/**
 * Conditional [ViewTreeObserver.removeOnGlobalLayoutListener]
 *
 * @param runLayout return an [RemoveAndValue], Check whether
 * [RemoveAndValue.removeCallback] judgment should be removed.
 *
 * @see [removeAndUnit]
 */
inline infix fun View.doOnLayoutWhen(crossinline runLayout: () -> RemoveAndValue<Unit>) {
    val vto = this.viewTreeObserver
    vto.addOnGlobalLayoutListener(object : ViewTreeObserver.OnGlobalLayoutListener {
        override fun onGlobalLayout() {
            val (removeCallback, _) = runLayout()

            if (removeCallback) {
                when {
                    vto.isAlive -> removeOnGlobalLayoutListener(vto, this)
                    else -> removeOnGlobalLayoutListener(viewTreeObserver, this)
                }
            }
        }
    })
}

/**
 * Conditional [ViewTreeObserver.removeOnGlobalLayoutListener]
 * (Note: this is just an example of a return value that is
 * not actually applied.)
 *
 * @param runPreDraw return an [RemoveAndValue], Check whether
 * [RemoveAndValue.removeCallback] judgment should be removed,
 * [RemoveAndValue.actualValue] is actual return value;
 *
 * @see [removeAndReturn]
 */
inline infix fun View.doOnPreDrawWhen(crossinline runPreDraw: () -> RemoveAndValue<Boolean>) {
    val vto = this.viewTreeObserver
    vto.addOnPreDrawListener(object : ViewTreeObserver.OnPreDrawListener {
        override fun onPreDraw(): Boolean {
            val (removeCallback, actualValue) = runPreDraw()

            if (removeCallback) {
                when {
                    vto.isAlive -> vto.removeOnPreDrawListener(this)
                    else -> vto.removeOnPreDrawListener(this)
                }
            }

            return actualValue
        }
    })
}
