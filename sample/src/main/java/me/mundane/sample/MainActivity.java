package me.mundane.sample;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.Log;

import me.mundane.sample.model.GankMeiziResult;
import me.mundane.sample.network.APIFactory;
import me.mundane.sample.network.RxSchedulersHelper;
import me.mundane.sample.network.api.GankAPI;
import me.mundane.sample.recycler.MeiZiAdapter;
import rx.Observable;
import rx.Subscriber;
import rx.functions.Action0;
import rx.schedulers.Schedulers;

public class MainActivity extends AppCompatActivity {


    private ProgressDialog mProgressDialog;
    private RecyclerView mRv;
    private MeiZiAdapter mMeiZiAdapter;
    private static final String TAG = "MainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mRv = (RecyclerView) findViewById(R.id.rv);
        mRv.setLayoutManager(new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL));
        GankAPI gankAPI = APIFactory.createGankAPI();
        // 这里的gankAPI.getMeiziData(10, 1)代替之前的call
        gankAPI.getMeiziData(10, 1)
                .compose(RxSchedulersHelper.<GankMeiziResult>io2main())
                .doOnSubscribe(new Action0() {
                    @Override
                    public void call() {
                        // 貌似doOnSubsribe的线程不受前面subscribeOn()指定线程的影响, 默认为主线程
                        Log.d(TAG, "currentThread = " + Thread.currentThread().getName());
                        showProgressDialog();
                    }
                })
                .subscribe(new Subscriber<GankMeiziResult>() {
                    @Override
                    public void onCompleted() {
                        hideProgressDialog();
                    }

                    @Override
                    public void onError(Throwable e) {
                        hideProgressDialog();
                    }

                    @Override
                    public void onNext(GankMeiziResult gankMeizhiResult) {
                        mMeiZiAdapter = new MeiZiAdapter(gankMeizhiResult.beauties);
                        mRv.setAdapter(mMeiZiAdapter);
                    }
                });

    }

    private void showProgressDialog() {
        mProgressDialog = new ProgressDialog(this);
        mProgressDialog.show();
    }

    private void hideProgressDialog() {
        mProgressDialog.dismiss();
    }

    private static <T> Observable.Transformer<T, T> applySchedulers() {
        return new Observable.Transformer<T, T>() {
            @Override
            public Observable<T> call(Observable<T> observable) {
                return observable.subscribeOn(Schedulers.io())
                        .observeOn(Schedulers.computation());
            }
        };
    }

}
