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

package com.youvigo.wms.warehouse;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.youvigo.wms.base.BaseViewModel;
import com.youvigo.wms.common.ResultState;
import com.youvigo.wms.data.backend.RetrofitClient;
import com.youvigo.wms.data.backend.api.SapService;
import com.youvigo.wms.data.dto.base.Additional;
import com.youvigo.wms.data.dto.base.ControlInfo;
import com.youvigo.wms.data.dto.request.MaterialQueryRequest;
import com.youvigo.wms.data.dto.request.MaterialQueryRequestDetails;
import com.youvigo.wms.data.entities.PositionMovementModelView;

import java.util.List;

public class PositionMovementViewModel extends BaseViewModel {

	private MutableLiveData<Boolean> _isLoading = new MutableLiveData<>(false);
	private MutableLiveData<ResultState> queryState = new MutableLiveData<>();
	private MutableLiveData<List<PositionMovementModelView>> _positions = new MutableLiveData<>();

	/**
	 * 查询数据
	 * @param material_coding        物料编码
	 * @param batch_number          物料批次号
	 * @param position         仓位
	 */
	void query(String material_coding, String batch_number, String position) {
		_isLoading.setValue(true);
		RetrofitClient retrofitClient = RetrofitClient.getInstance();
		SapService sapService = retrofitClient.getSapService();
		// 构建请求
		MaterialQueryRequest materialQueryRequest = new MaterialQueryRequest();
		materialQueryRequest.setControlInfo(new ControlInfo());
		// 请求体
		MaterialQueryRequestDetails materQueryRequestDetails = new MaterialQueryRequestDetails();
		materQueryRequestDetails.setMATNR(material_coding); // 物料编码
		materQueryRequestDetails.setCHARG(batch_number); // 批次
		materQueryRequestDetails.setMAKTX(""); // 物料描述
		materQueryRequestDetails.setZZCOMMONNAME(""); // 通用名称
		materQueryRequestDetails.setZZDRUGSPEC(""); //规格
		materQueryRequestDetails.setLGPLA(position); // 仓位
		materQueryRequestDetails.setWERKS(retrofitClient.getFactoryCode());// 工厂
		materQueryRequestDetails.setLGORT(retrofitClient.getStockLocationCode()); // 库存的
		materQueryRequestDetails.setLGNUM(retrofitClient.getWarehouseNumber()); // 仓库
		materQueryRequestDetails.setADDITIONAL(new Additional());
		materialQueryRequest.setMaterialQueryRequestDetails(materQueryRequestDetails);

//		Call<MaterialQueryResult> materials = sapService.materialQuery(materialQueryRequest);
//		materials.enqueue(new Callback<MaterialQueryResult>() {
//			@Override
//			public void onResponse(@NotNull Call<MaterialQueryResult> call, @NotNull Response<MaterialQueryResult> response) {
//				if (response.isSuccessful()) {
//					MaterialQueryResult materialQueryResult = response.body();
//					assert materialQueryResult != null;
//					if (!materialQueryResult.getMaterialQueryResponse().getMessage().getSuccess().equalsIgnoreCase("S")) {
//						queryState.setValue(new ResultState(false, materialQueryResult.getMaterialQueryResponse().getMessage().getMessage()));
//						_isLoading.setValue(false);
//						return;
//					}
////					if (materialQueryResult.getMaterialQueryResponse().getData()== null)
////					{
////						Timber.e(materialQueryResult.getMaterialQueryResponse().getResult().getMessage());
////						return;
////					}
//					Disposable disposable = Flowable.create((FlowableOnSubscribe<List<PositionMovementModelView>>) emitter -> {
//						List<PositionMovementModelView> PositionData = new ArrayList<>();
//						List<StockMaterial> data = materialQueryResult.getMaterialQueryResponse().getData();
//						// 组织数据
//						for (StockMaterial m : data) {
//							PositionMovementModelView position = new PositionMovementModelView();
//							position.setLGNUM(m.getWarehouseNumber()); //仓库号
//							position.setTAPOS(m.getZZAUFNR()); // 行项目号
//							position.setMATNR(m.getMaterialCode()); // 物料编码
//							position.setZZCOMMONNAME(m.getMaterialCommonName());// 物料通用名称
//							position.setMAKTX(m.getMaterialDescription());// 物料描述
//							position.setWERKS(m.getFactoryCode());// 工厂
//							position.setLGORT(retrofitClient.getStockLocationCode());// 库存地点
//							position.setCHARG( m.getBatchNumber());// 批号
//							position.setMEINS_TXT(m.getBaseUnitTxt()); //基本单位文本
//							position.setMEINS(m.getBaseUnit()); //基本单位
//							position.setVSOLM(""); //基本单位数量
//							position.setALTME(""); //辅助单位
//							position.setVSOLA(""); //辅助单位数量
//							position.setVLTYP(m.getLGTYP()); // 下架仓位类型
//							position.setNLTYP(""); // 上架仓位类型
//							position.setNLPLA(""); // 上架仓位
//							position.setVLPLA(m.getCargoSpace()); // 下架仓位
//							position.setZZPACKAGING(m.getZZPACKAGING()); // 是否合箱
//							position.setZZLICHA_MAIN(m.getZZLICHA_MAIN()); // 主批次
//							position.setZZMENGE_MAIN(m.getZZMENGE_MAIN()); // 主批次数量
//							position.setZZLICHA_AUXILIARY(m.getZZLICHA_AUXILIARY()); // 辅批次
//							position.setZZMENGE_AUXILIARY(m.getZZMENGE_AUXILIARY()); // 辅批次数量
//							position.setZZLICHA(m.getZZLICHA()); // 供应商批次
//							position.setVLTYP(m.getZZLICHA()); // 下架仓位类型
//							position.setVERME(m.getActualInventory()); // 可用库存量
//							position.setZZDRUGSPEC(m.getSpecification()); // 规格
//							position.setBESTQ(m.getBESTQ()); // 库存类别
//							PositionData.add(position);
//						}
//						emitter.onNext(PositionData);
//						emitter.onComplete();
//					}, BackpressureStrategy.LATEST).subscribeOn(Schedulers.io())
//							.observeOn(AndroidSchedulers.mainThread())
//							.subscribeWith(new DisposableSubscriber<List<PositionMovementModelView>>() {
//						@Override
//						public void onNext(List<PositionMovementModelView> positions) {
//							_positions.setValue(positions);
//						}
//
//						@Override
//						public void onError(Throwable t) {
//							_isLoading.setValue(false);
//						}
//
//						@Override
//						public void onComplete() {
//							_isLoading.setValue(false);
//						}
//					});
//					addSubscription(disposable);
//				}
//			}
//
//			@Override
//			public void onFailure(@NotNull Call<MaterialQueryResult> call, @NotNull Throwable t) {
//				queryState.setValue(new ResultState(false, t.getMessage()));
//				Timber.e(t);
//			}
//		});
	}

	LiveData<Boolean> isLoading() {
		return _isLoading;
	}

	LiveData<List<PositionMovementModelView>> positions() { return _positions; }

	LiveData<ResultState> getQueryState() {
		return queryState;
	}

}
