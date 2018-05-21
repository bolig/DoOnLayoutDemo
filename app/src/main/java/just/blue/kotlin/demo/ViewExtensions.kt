package just.blue.kotlin.demo

import android.os.Build
import android.view.View
import android.view.ViewTreeObserver

/** Plan1 */

/**
 * Conditional [ViewTreeObserver.removeOnGlobalLayoutListener]
 *
 * @param runLayout run Layout
 * @param removePredicate return true, remove callback; Otherwise, no operation
 */
inline fun View.doOnLayout(crossinline removePredicate: () -> Boolean = { false }, crossinline runLayout: () -> Unit) {
    this.viewTreeObserver.addOnGlobalLayoutListener(object : ViewTreeObserver.OnGlobalLayoutListener {
        override fun onGlobalLayout() {
            kotlin.run(runLayout)

            if (kotlin.run(removePredicate)) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                    viewTreeObserver.removeOnGlobalLayoutListener(this)
                } else {
                    viewTreeObserver.removeGlobalOnLayoutListener(this)
                }
            }
        }
    })
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
    this.viewTreeObserver.addOnGlobalLayoutListener(object : ViewTreeObserver.OnGlobalLayoutListener {
        override fun onGlobalLayout() {
            val (removeCallback, _) = runLayout()

            if (removeCallback) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                    viewTreeObserver.removeOnGlobalLayoutListener(this)
                } else {
                    viewTreeObserver.removeGlobalOnLayoutListener(this)
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
    this.viewTreeObserver.addOnPreDrawListener(object : ViewTreeObserver.OnPreDrawListener {
        override fun onPreDraw(): Boolean {
            val (removeCallback, actualValue) = runPreDraw()

            if (removeCallback) {
                viewTreeObserver.removeOnPreDrawListener(this)
            }

            return actualValue
        }
    })
}
