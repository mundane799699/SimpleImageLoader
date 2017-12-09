package me.mundane.sample;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;

import me.mundane.sample.model.GankMeiziResult;
import me.mundane.sample.network.APIFactory;
import me.mundane.sample.network.api.GankAPI;
import me.mundane.sample.recycler.MeiZiAdapter;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action0;
import rx.schedulers.Schedulers;

public class MainActivity extends AppCompatActivity {
	
	
	private ProgressDialog mProgressDialog;
	private RecyclerView mRv;
	private MeiZiAdapter mMeiZiAdapter;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		mRv = (RecyclerView) findViewById(R.id.rv);
		mRv.setLayoutManager(new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL));
		GankAPI gankAPI = APIFactory.createGankAPI();
		// 这里的gankAPI.getMeiziData(10, 1)代替之前的call
		gankAPI.getMeiziData(10, 1)
		    .subscribeOn(Schedulers.io())
		    .doOnSubscribe(new Action0() {
			    @Override
			    public void call() {
				    showProgressDialog();
			    }
		    })
		    .subscribeOn(AndroidSchedulers.mainThread())  // 指定doOnSubscribe()所发生的线程, 其实这句代码可以不加的
		    .observeOn(AndroidSchedulers.mainThread())
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
}
