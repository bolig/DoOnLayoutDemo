package just.blue.kotlin.demo

import android.os.Bundle
import android.os.Handler
import android.support.v7.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        Handler().postDelayed({
            iv_img.setImageResource(R.mipmap.ic_launcher_round)
        }, 2000)

        // Plan1 example
        iv_img.doOnLayout({
            iv_img.drawable != null
        }) {
            // doThing ...
        }

        // Plan2 example
        iv_img.doOnLayoutWhen {
            var hasDrawable = iv_img.drawable != null

            if (hasDrawable) {
                // doThing ...
            }

            hasDrawable.removeAndUnit()
        }

//        iv_img.doOnPreDrawWhen {
//            var hasDrawable = iv_img.drawable != null
//
//            if (hasDrawable) {
//                // doThing ...
//            }
//
//            hasDrawable removeAndReturn false
//        }
    }
}
