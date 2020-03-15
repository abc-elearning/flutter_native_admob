package com.nover.flutternativeadmob

import android.content.Context
import android.graphics.Color
import android.graphics.PorterDuff
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import com.google.android.gms.ads.formats.MediaView
import com.google.android.gms.ads.formats.UnifiedNativeAd
import com.google.android.gms.ads.formats.UnifiedNativeAdView

class NativeAdView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : LinearLayout(context, attrs, defStyleAttr) {

  var options = NativeAdmobOptions()
    set(value) {
      field = value
      updateOptions()
    }

  private val adView: UnifiedNativeAdView

  private val ratingBar: RatingBar

  private val adMedia: MediaView

  private val adHeadline: TextView
  private val adAdvertiser: TextView
  private val adBody: TextView
  private val adPrice: TextView
  private val adStore: TextView
  private val adAttribution: TextView
  private val callToAction: Button

  init {
    val inflater = LayoutInflater.from(context)
    inflater.inflate(R.layout.native_admob_banner_view, this, true)

    setBackgroundColor(Color.TRANSPARENT)

    adView = findViewById(R.id.ad_view)

    adMedia = adView.findViewById(R.id.ad_media)

    adHeadline = adView.findViewById(R.id.ad_headline)
    adAdvertiser = adView.findViewById(R.id.ad_advertiser)
    adBody = adView.findViewById(R.id.ad_body)
    adPrice = adView.findViewById(R.id.ad_price)
    adStore = adView.findViewById(R.id.ad_store)
    adAttribution = adView.findViewById(R.id.ad_attribution)

    ratingBar = adView.findViewById(R.id.ad_stars)

    adAttribution.background = Color.parseColor("#FFCC66").toRoundedColor(3f)
    callToAction = adView.findViewById(R.id.ad_call_to_action)

    initialize()
  }

  private fun initialize() {
    // The MediaView will display a video asset if one is present in the ad, and the
    // first image asset otherwise.
    adView.mediaView = adMedia

    // Register the view used for each individual asset.
    adView.headlineView = adHeadline
    adView.bodyView = adBody
    adView.callToActionView = callToAction
    adView.iconView = adView.findViewById(R.id.ad_icon)
    adView.priceView = adPrice
    adView.starRatingView = ratingBar
    adView.storeView = adStore
    adView.advertiserView = adAdvertiser
  }

  fun setNativeAd(nativeAd: UnifiedNativeAd?) {
    if (nativeAd == null) return

    // Some assets are guaranteed to be in every UnifiedNativeAd.
    adMedia.setMediaContent(nativeAd.mediaContent)
    adMedia.setImageScaleType(ImageView.ScaleType.FIT_CENTER)

    adHeadline.text = nativeAd.headline
    adBody.text = nativeAd.body
    (adView.callToActionView as Button).text = nativeAd.callToAction

    // These assets aren't guaranteed to be in every UnifiedNativeAd, so it's important to
    // check before trying to display them.
    val icon = nativeAd.icon

    if (icon == null) {
      adView.iconView.visibility = View.GONE
    } else {
      (adView.iconView as ImageView).setImageDrawable(icon.drawable)
      adView.iconView.visibility = View.VISIBLE
    }

    if (nativeAd.price == null) {
      adPrice.visibility = View.INVISIBLE
    } else {
      adPrice.visibility = View.VISIBLE
      adPrice.text = nativeAd.price
    }

    if (nativeAd.store == null) {
      adStore.visibility = View.INVISIBLE
    } else {
      adStore.visibility = View.VISIBLE
      adStore.text = nativeAd.store
    }

    if (nativeAd.starRating == null) {
      adView.starRatingView.visibility = View.INVISIBLE
    } else {
      (adView.starRatingView as RatingBar).rating = nativeAd.starRating!!.toFloat()
      adView.starRatingView.visibility = View.VISIBLE
    }

    if (nativeAd.advertiser == null) {
      adAdvertiser.visibility = View.INVISIBLE
    } else {
      adAdvertiser.visibility = View.VISIBLE
      adAdvertiser.text = nativeAd.advertiser
    }

    // Assign native ad object to the native view.
    adView.setNativeAd(nativeAd)
  }

  private fun updateOptions() {
    adMedia.visibility = if (options.showMediaContent) View.VISIBLE else View.GONE

    ratingBar.progressDrawable
        .setColorFilter(options.ratingColor, PorterDuff.Mode.SRC_ATOP)

    options.adLabelOptions.backgroundColor?.let {
      adAttribution.background = it.toRoundedColor(3f)
    }
    adAttribution.textSize = options.adLabelOptions.fontSize
    adAttribution.setTextColor(options.adLabelOptions.color)

    adHeadline.setTextColor(options.headlineTextOptions.color)
    adHeadline.textSize = options.headlineTextOptions.fontSize

    adAdvertiser.setTextColor(options.advertiserTextOptions.color)
    adAdvertiser.textSize = options.advertiserTextOptions.fontSize

    adBody.setTextColor(options.bodyTextOptions.color)
    adBody.textSize = options.bodyTextOptions.fontSize

    adStore.setTextColor(options.storeTextOptions.color)
    adStore.textSize = options.storeTextOptions.fontSize

    adPrice.setTextColor(options.priceTextOptions.color)
    adPrice.textSize = options.priceTextOptions.fontSize

    callToAction.setTextColor(options.callToActionOptions.color)
    callToAction.textSize = options.callToActionOptions.fontSize
    options.callToActionOptions.backgroundColor?.let {
      callToAction.setBackgroundColor(it)
    }
  }
}