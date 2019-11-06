package io.github.keep2iron.daydream

import android.app.Application
import com.orhanobut.logger.AndroidLogAdapter
import com.orhanobut.logger.Logger
import com.orhanobut.logger.PrettyFormatStrategy
import io.github.keep2iron.fast4android.arch.swipe.ParallaxBackApplicationTask
import io.github.keep2iron.fast4android.core.DefaultLogger
import io.github.keep2iron.fast4android.core.Fast4Android
import io.github.keep2iron.pineapple.ImageLoaderManager
import io.github.keep2iron.pomelo.NetworkManager
import io.github.keep2iron.pomelo.convert.CustomGsonConvertFactory
import io.github.keep2iron.pomelo.log.NetworkLoggingInterceptor
import io.reactivex.schedulers.Schedulers
import okhttp3.Protocol
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import java.util.Collections
import java.util.concurrent.TimeUnit

class DayDreamApplication : Application() {
    override fun onCreate() {
        super.onCreate()

        Fast4Android.init(this){
            logger(DefaultLogger())
            applicationInitTask(ParallaxBackApplicationTask())
        }

        initNetwork()

        initImageLoader()
    }

    private fun initImageLoader() {
        ImageLoaderManager.init(this)
    }

    private fun initNetwork() {
        val formatStrategy = PrettyFormatStrategy.newBuilder()
            .showThreadInfo(false)  // (Optional) Whether to show thread info or not. Default true
            .methodOffset(2)        // (Optional) Hides internal method calls up to offset. Default 5
            .tag("pomelo")   // (Optional) Global tag for every log. Default PRETTY_LOGGER
            .build()
        Logger.addLogAdapter(AndroidLogAdapter(formatStrategy))

        NetworkManager.init("http://10.8.1.148:8080") {
            initOkHttp {
                protocols(Collections.singletonList(Protocol.HTTP_1_1))
                //解决 https://www.cnblogs.com/myhalo/p/6811472.html
                connectTimeout(15, TimeUnit.SECONDS)
                readTimeout(15, TimeUnit.SECONDS)
                //optional network log.
                addNetworkInterceptor(NetworkLoggingInterceptor(object :
                    NetworkLoggingInterceptor.Logger {
                    override fun d(message: String) {
                        Logger.d(message)
                    }
                }))
            }

            initRetrofit {
                //custom gson convert Factory
                addConverterFactory(CustomGsonConvertFactory.create())
                addCallAdapterFactory(RxJava2CallAdapterFactory.createWithScheduler(Schedulers.io()))
            }
        }
    }
}