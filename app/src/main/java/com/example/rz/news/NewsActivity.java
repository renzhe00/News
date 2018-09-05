package com.example.rz.news;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBar;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.example.rz.news.gson.News;
import com.example.rz.news.gson.NewsList;
import com.example.rz.news.utils.HttpUtil;
import com.example.rz.news.utils.Utility;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

/**
 *
 */
public class NewsActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    //频道标记
    private static final int  ITEM_SOCIETY= 1;
    private static final int  ITEM_COUNTY= 2;
    private static final int  ITEM_INTERNATIONAL= 3;
    private static final int  ITEM_FUN= 4;
    private static final int  ITEM_SPORT= 5;
    private static final int  ITEM_NBA= 6;
    private static final int  ITEM_FOOTBALL= 7;
    private static final int  ITEM_TECHNOLOGY= 8;
    private static final int  ITEM_WORK= 9;
    private static final int  ITEM_APPLE= 10;
    private static final int  ITEM_MILITARY= 11;
    private static final int  ITEM_INTERNET= 12;
    private static final int  ITEM_TRAVEL= 13;
    private static final int  ITEM_HEALTH= 14;
    private static final int  ITEM_STRANGE= 15;
    private static final int  ITEM_BEAUTY= 16;
    private static final int  ITEM_VR= 17;
    private static final int  ITEM_IT= 18;


    private List<Summary> mSummaryList = new ArrayList<>();

    //ListView 适配器
    private SummaryAdapter mAdapter;

    @BindView(R.id.toolbar)
    Toolbar mToolbar;
    @BindView(R.id.swipe_refresh)
    SwipeRefreshLayout mRefreshLayout;
    @BindView(R.id.list_view)
    ListView mListView;
    @BindView(R.id.drawer_layout)
    DrawerLayout mDrawerLayout;
    @BindView(R.id.nav_view)
    NavigationView mNavigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_news);
        ButterKnife.bind(this);

        //标题栏
        setSupportActionBar(mToolbar);
        final ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setDisplayShowTitleEnabled(true);
            actionBar.setHomeAsUpIndicator(R.drawable.ic_menu);
            actionBar.setTitle(R.string.society);
        }

        // 下拉刷新
        mRefreshLayout.setColorSchemeColors(getResources().getColor(R.color.colorPrimary));
        mRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                mRefreshLayout.setRefreshing(true);
                int item = parseString((String)actionBar.getTitle());
                requestNews(item);
            }
        });

        requestNews(ITEM_SOCIETY);

        // ListView
        mAdapter = new SummaryAdapter(this, R.layout.news_item, mSummaryList);
        mListView.setAdapter(mAdapter);
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            Intent mIntent = new Intent(NewsActivity.this, ContentActivity.class);
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Summary summary = mSummaryList.get(position);
                mIntent.putExtra("title", actionBar.getTitle());
                mIntent.putExtra("url", summary.getUrl());
                startActivity(mIntent);
            }
        });

        mNavigationView.setCheckedItem(R.id.nav_society);
        mNavigationView.setNavigationItemSelectedListener(this);
    }


    /**
     * 判断页面内容和标题是否一致，否则重新请求数据
     * @param text
     * @param item
     */
    private void handleCurrentPage(String text, int item) {
        ActionBar actionBar = getSupportActionBar();
        if(!text.equals(actionBar.getTitle().toString())) {
            actionBar.setTitle(String.valueOf(text));
            requestNews(item);
            mRefreshLayout.setRefreshing(true);
        }
    }


    /**
     * 请求处理数据
     * @param item
     */
    public void requestNews(int item) {

        String address = response(item);
        HttpUtil.sendOkHttpRequest(address, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(NewsActivity.this, "新闻加载失败", Toast.LENGTH_SHORT)
                                .show();
                    }
                });
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                final String responseText = response.body().string();
                final NewsList newsList = Utility.parseJsonWithGson(responseText);
                final int code = newsList.getCode();
                final String msg = newsList.getMsg();
                if (code == 200) {
                    mSummaryList.clear();
                    for (News news:newsList.newsList) {
                        Summary summary = new Summary(news.getTitle(), news.getDescription(),
                                news.getTime(), news.getPicUrl(), news.getUrl());
                        mSummaryList.add(summary);
                    }

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if (mAdapter != null) {
                                mAdapter.notifyDataSetChanged();
                                mListView.setSelection(0);
                                mRefreshLayout.setRefreshing(false);
                            }
                        }
                    });
                } else {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(NewsActivity.this,
                                    String.valueOf(code) + "数据返回错误",
                                    Toast.LENGTH_SHORT).show();
                            mRefreshLayout.setRefreshing(false);
                        }
                    });
                }
            }
        });
    }


    /**
     * @param item
     * @return url链接
     */
    private String response(int item) {
        String address = "https://api.tianapi.com/social/?key=efcf41d1883e2266e4b85324fb411100&num=50&rand=1";
        switch (item) {
            case ITEM_SOCIETY:
                break;
            case ITEM_COUNTY:
                address = address.replaceAll("social","guonei");
                break;
            case ITEM_INTERNATIONAL:
                address = address.replaceAll("social","world");
                break;
            case ITEM_FUN:
                address = address.replaceAll("social","huabian");
                break;
            case ITEM_SPORT:
                address = address.replaceAll("social","tiyu");
                break;
            case ITEM_NBA:
                address = address.replaceAll("social","nba");
                break;
            case ITEM_FOOTBALL:
                address = address.replaceAll("social","football");
                break;
            case ITEM_TECHNOLOGY:
                address = address.replaceAll("social","keji");
                break;
            case ITEM_WORK:
                address = address.replaceAll("social","startup");
                break;
            case ITEM_APPLE:
                address = address.replaceAll("social","apple");
                break;
            case ITEM_MILITARY:
                address = address.replaceAll("social","military");
                break;
            case ITEM_INTERNET:
                address = address.replaceAll("social","mobile");
                break;
            case ITEM_TRAVEL:
                address = address.replaceAll("social","travel");
                break;
            case ITEM_HEALTH:
                address = address.replaceAll("social","health");
                break;
            case ITEM_STRANGE:
                address = address.replaceAll("social","qiwen");
                break;
            case ITEM_BEAUTY:
                address = address.replaceAll("social","meinv");
                break;
            case ITEM_VR:
                address = address.replaceAll("social","vr");
                break;
            case ITEM_IT:
                address = address.replaceAll("social","it");
                break;
            default:
        }
        return address;
    }


    /**
     * @param text
     * @return 返回对应的item
     */
    private int parseString(String text) {
        if (text.equals("社会新闻")){
            return ITEM_SOCIETY;
        }
        if (text.equals("国内新闻")){
            return ITEM_COUNTY;
        }
        if (text.equals("国际新闻")){
            return ITEM_INTERNATIONAL;
        }
        if (text.equals("娱乐新闻")){
            return ITEM_FUN;
        }
        if (text.equals("体育新闻")){
            return ITEM_SPORT;
        }
        if (text.equals("NBA新闻")){
            return ITEM_NBA;
        }
        if (text.equals("足球新闻")){
            return ITEM_FOOTBALL;
        }
        if (text.equals("科技新闻")){
            return ITEM_TECHNOLOGY;
        }
        if (text.equals("创业新闻")){
            return ITEM_WORK;
        }
        if (text.equals("苹果新闻")){
            return ITEM_APPLE;
        }
        if (text.equals("军事新闻")){
            return ITEM_MILITARY;
        }
        if (text.equals("移动互联")){
            return ITEM_INTERNET;
        }
        if (text.equals("旅游资讯")){
            return ITEM_TRAVEL;
        }
        if (text.equals("健康知识")){
            return ITEM_HEALTH;
        }
        if (text.equals("奇闻异事")){
            return ITEM_STRANGE;
        }
        if (text.equals("美女图片")){
            return ITEM_BEAUTY;
        }
        if (text.equals("VR科技")){
            return ITEM_VR;
        }
        if (text.equals("IT资讯")){
            return ITEM_IT;
        }
        return ITEM_SOCIETY;
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
        switch (menuItem.getItemId()) {
            case R.id.nav_society:
                handleCurrentPage("社会新闻",ITEM_SOCIETY);
                break;
            case R.id.nav_county:
                handleCurrentPage("国内新闻",ITEM_COUNTY);
                break;
            case R.id.nav_international:
                handleCurrentPage("国际新闻",ITEM_INTERNATIONAL);
                break;
            case R.id.nav_fun:
                handleCurrentPage("娱乐新闻",ITEM_FUN);
                break;
            case R.id.nav_sport:
                handleCurrentPage("体育新闻",ITEM_SPORT);
                break;
            case R.id.nav_nba:
                handleCurrentPage("NBA新闻",ITEM_NBA);
                break;
            case R.id.nav_football:
                handleCurrentPage("足球新闻",ITEM_FOOTBALL);
                break;
            case R.id.nav_technology:
                handleCurrentPage("科技新闻",ITEM_TECHNOLOGY);
                break;
            case R.id.nav_work:
                handleCurrentPage("创业新闻",ITEM_WORK);
                break;
            case R.id.nav_apple:
                handleCurrentPage("苹果新闻",ITEM_APPLE);
                break;
            case R.id.nav_military:
                handleCurrentPage("军事新闻",ITEM_MILITARY);
                break;
            case R.id.nav_internet:
                handleCurrentPage("移动互联",ITEM_INTERNET);
                break;
            case R.id.nav_travel:
                handleCurrentPage("旅游资讯",ITEM_TRAVEL);
                break;
            case R.id.nav_health:
                handleCurrentPage("健康知识",ITEM_HEALTH);
                break;
            case R.id.nav_strange:
                handleCurrentPage("奇闻异事",ITEM_STRANGE);
                break;
            case R.id.nav_beauty:
                handleCurrentPage("美女图片",ITEM_BEAUTY);
                break;
            case R.id.nav_vr:
                handleCurrentPage("VR科技",ITEM_VR);
                break;
            case R.id.nav_it:
                handleCurrentPage("IT资讯",ITEM_IT);
                break;
            default:
                break;
        }
        mDrawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.news, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:
                mDrawerLayout.openDrawer(GravityCompat.START);
                break;
            case R.id.action_settings:
                Toast.makeText(NewsActivity.this, "Settings Testing",
                        Toast.LENGTH_SHORT).show();
            default:
        }
        return true;
    }
}
