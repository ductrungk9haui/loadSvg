package Glid;

import android.app.Activity;
import android.content.Context;
import android.graphics.Picture;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.PictureDrawable;
import android.net.Uri;
import android.view.View;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.bumptech.glide.RequestBuilder;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.Options;
import com.bumptech.glide.load.ResourceDecoder;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.load.engine.Resource;
import com.bumptech.glide.load.resource.SimpleResource;
import com.bumptech.glide.load.resource.transcode.ResourceTranscoder;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.ImageViewTarget;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.target.Target;
import com.bumptech.glide.request.transition.Transition;
import com.caverock.androidsvg.SVG;
import com.caverock.androidsvg.SVGParseException;

import java.io.IOException;
import java.io.InputStream;

public class GlideToVectorYou {

    private static GlideToVectorYou instance;

    private RequestBuilder<PictureDrawable> requestBuilder;
    private int placeHolderLoading = -1;
    private int placeHolderError = -1;

    public static GlideToVectorYou init() {
        if (instance == null) {
            instance = new GlideToVectorYou();
        }

        return instance;
    }

    public GlideToVectorYou with(Context ctx) {
        createRequestBuilder(ctx);

        return instance;
    }

    public GlideToVectorYou withListener(GlideToVectorYouListener listener) {
        requestBuilder.listener(new SvgSoftwareLayerSetter(listener));

        return instance;
    }

    public GlideToVectorYou setPlaceHolder(int placeHolderLoading, int placeHolderError) {
        this.placeHolderError = placeHolderError;
        this.placeHolderLoading = placeHolderLoading;

        return instance;
    }

    public void load(Uri uri, ImageView imageView) {
        if (placeHolderLoading != -1 && placeHolderError != -1) {
            requestBuilder.apply(
                    new RequestOptions()
                            .placeholder(placeHolderLoading)
                            .error(placeHolderError)
            );
        }

        requestBuilder.load(uri).into(imageView);

    }

    public static void justLoadImage(Activity activity, Uri uri, ImageView imageView) {
        GlideApp.with(activity)
                .as(PictureDrawable.class)
                .listener(new SvgSoftwareLayerSetter())
                .load(uri).into(imageView);
    }

    public static void justLoadImageAsBackground(Activity activity, Uri uri, final View view) {
        GlideApp.with(activity).load(uri).into(new SimpleTarget<Drawable>() {
            @Override
            public void onResourceReady(@NonNull Drawable resource, Transition<? super Drawable> transition) {
                view.setBackground(resource);
            }
        });
    }

    public RequestBuilder<PictureDrawable> getRequestBuilder() {
        return requestBuilder;
    }

    private void createRequestBuilder(Context ctx) {
        requestBuilder = GlideApp.with(ctx)
                .as(PictureDrawable.class)
                .diskCacheStrategy(DiskCacheStrategy.DATA)
                .listener(new SvgSoftwareLayerSetter());
    }

    public interface GlideToVectorYouListener {
        void onLoadFailed();

        void onResourceReady();
    }

    /**
     * Decodes an SVG internal representation from an {@link InputStream}.
     */
    public static class SvgDecoder implements ResourceDecoder<InputStream, SVG> {

        @Override
        public boolean handles(@NonNull InputStream source, @NonNull Options options) {
            // TODO: Can we tell?
            return true;
        }

        public Resource<SVG> decode(@NonNull InputStream source, int width, int height,
                                    @NonNull Options options)
                throws IOException {
            try {
                SVG svg = SVG.getFromInputStream(source);
                return new SimpleResource<>(svg);
            } catch (SVGParseException ex) {
                throw new IOException("Cannot load SVG from stream", ex);
            }
        }

    }

    /**
     * Convert the {@link SVG}'s internal representation to an Android-compatible one
     * ({@link Picture}).
     */
    public static class SvgDrawableTranscoder implements ResourceTranscoder<SVG, PictureDrawable> {
        @Nullable
        @Override
        public Resource<PictureDrawable> transcode(@NonNull Resource<SVG> toTranscode,
                                                   @NonNull Options options) {
            SVG svg = toTranscode.get();
            Picture picture = svg.renderToPicture();
            PictureDrawable drawable = new PictureDrawable(picture);
            return new SimpleResource<>(drawable);
        }
    }

    /**
     * Listener which updates the {@link ImageView} to be software rendered, because
     * {@link com.caverock.androidsvg.SVG SVG}/{@link android.graphics.Picture Picture} can't render on
     * a hardware backed {@link android.graphics.Canvas Canvas}.
     */
    public static class SvgSoftwareLayerSetter implements RequestListener<PictureDrawable> {

        GlideToVectorYou.GlideToVectorYouListener customListener;

        SvgSoftwareLayerSetter(GlideToVectorYou.GlideToVectorYouListener listener) {
            this.customListener = listener;
        }


        SvgSoftwareLayerSetter() {
        }

        @Override
        public boolean onLoadFailed(GlideException e, Object model, Target<PictureDrawable> target,
                                    boolean isFirstResource) {
            ImageView view = ((ImageViewTarget<?>) target).getView();
            view.setLayerType(ImageView.LAYER_TYPE_NONE, null);

            if (customListener != null) {
                customListener.onLoadFailed();
            }
            return false;
        }

        @Override
        public boolean onResourceReady(PictureDrawable resource, Object model,
                                       Target<PictureDrawable> target, DataSource dataSource, boolean isFirstResource) {
            ImageView view = ((ImageViewTarget<?>) target).getView();
            view.setLayerType(ImageView.LAYER_TYPE_SOFTWARE, null);

            if (customListener != null) {
                customListener.onResourceReady();
            }

            return false;
        }
    }
}
