/*
 * Copyright (c) 2020. komamj
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.youvigo.wms.search;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.youvigo.wms.base.BaseViewModel;
import com.youvigo.wms.data.entities.MaterialVoucher;
import com.youvigo.wms.data.entities.Shelving;
import com.youvigo.wms.data.source.Repository;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import io.reactivex.BackpressureStrategy;
import io.reactivex.Flowable;
import io.reactivex.FlowableOnSubscribe;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import io.reactivex.subscribers.DisposableSubscriber;

class SearchViewModel extends BaseViewModel {

    private MutableLiveData<Boolean> _isLoading = new MutableLiveData<Boolean>(false);

    private MutableLiveData<List<MaterialVoucher>> _materials = new MutableLiveData<List<MaterialVoucher>>();

    private Repository repository;

    /**
     * 查询数据
     *
     * @param startDate        开始日期
     * @param endDate          结束日期
     * @param materialDocument 物料编号
     */
    void query(String startDate, String endDate, String materialDocument) {
        _isLoading.setValue(true);

//        ShelvingQueryRequest shelvingQueryRequest = new ShelvingQueryRequest();
//        shelvingQueryRequest.setControlInfo(new ControlInfo());
//        ShelvingQueryRequestDetails requestDetails = new ShelvingQueryRequestDetails();
//        requestDetails.setStartDate(startDate);
//        requestDetails.setEndDate(endDate);
//        requestDetails.setYear("2020");
//        requestDetails.setMaterialVoucherCode(materialDocument != null && !materialDocument.isEmpty() ? materialDocument : "");
//        requestDetails.setStockLocationCode("FZ01");
//        requestDetails.setWarehouseNumber("X01");
//        shelvingQueryRequest.setRequestDetails(requestDetails);
//
//        List<MaterialVoucher> shelvings = remoteDataSource.getShelvings(shelvingQueryRequest);

        Disposable disposable = Flowable.create((FlowableOnSubscribe<List<MaterialVoucher>>) emitter -> {
            List<MaterialVoucher> mockData = new ArrayList<>();
            for (int i = 0; i < 100; i++) {
                MaterialVoucher materialVoucher = new MaterialVoucher();
                materialVoucher.sourceUnit = "ThoughtWorks";
                materialVoucher.date = "2020-1-10";
                materialVoucher.materialDocument = "10102030007600000" + i;
                materialVoucher.creator = "我是谁" + i;
                materialVoucher.shelvings = produceShelvings(i);
                mockData.add(materialVoucher);
            }
            emitter.onNext(mockData);
            emitter.onComplete();
        }, BackpressureStrategy.LATEST)
                .delay(5, TimeUnit.SECONDS)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(new DisposableSubscriber<List<MaterialVoucher>>() {
                    @Override
                    public void onNext(List<MaterialVoucher> materials) {
                        _materials.setValue(materials);
                    }

                    @Override
                    public void onError(Throwable t) {
                        _isLoading.setValue(false);
                    }

                    @Override
                    public void onComplete() {
                        _isLoading.setValue(false);
                    }
                });
        addSubscription(disposable);
    }

    @NotNull
    private List<Shelving> produceShelvings(int i) {
        List<Shelving> shelvings = new ArrayList<>();
        for (int j = 0; j <= i; j++) {
            Shelving shelving = new Shelving();
            shelving.itemNumber = "1010201111100000" + j;
            shelving.commonName = "吸氧剂";
            shelving.lotNumber = "O12340000" + j;
            shelvings.add(shelving);
        }
        return shelvings;
    }

    LiveData<Boolean> isLoading() {
        return _isLoading;
    }

    LiveData<List<MaterialVoucher>> materials() {
        return _materials;
    }
}
