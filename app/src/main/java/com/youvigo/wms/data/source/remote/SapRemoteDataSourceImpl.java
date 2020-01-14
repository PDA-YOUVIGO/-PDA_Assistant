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

package com.youvigo.wms.data.source.remote;

import com.google.gson.Gson;
import com.youvigo.wms.data.entities.MaterialVoucher;
import com.youvigo.wms.data.dto.ShelvingQueryRequest;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.TimeUnit;

import okhttp3.Credentials;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class SapRemoteDataSourceImpl implements ISapRemoteDataSource {

	private final MediaType JSON = MediaType.parse("application/json; charset=utf-8");
	private final String TAG = this.getClass().getSimpleName();
	private final int TIME_OUT = 60;
	private OkHttpClient mOkHttpClient;
	private Gson mGson = new Gson();

	@Override
	public List<MaterialVoucher> getShelvings(ShelvingQueryRequest queryRequest) {

		String url = String.format("http://%s/RESTAdapter/PDA/On_The_Shelf", "52.82.87.90:50000");
		String username = "zengzx";
		String password = "abcd1234";
		String postJson = mGson.toJson(queryRequest);

		mOkHttpClient = new OkHttpClient.Builder().authenticator((route, response) -> {
			String credential = Credentials.basic(username, password);
			return response.request().newBuilder().header("Authorization", credential).build();
		}).connectTimeout(TIME_OUT, TimeUnit.SECONDS).writeTimeout(TIME_OUT, TimeUnit.SECONDS).readTimeout(TIME_OUT, TimeUnit.SECONDS).retryOnConnectionFailure(true).build();

		RequestBody body = RequestBody.create(postJson, JSON);
		Request.Builder builder = new Request.Builder();
		Request request = builder.url(url).post(body).build();

		try {
			Response response = mOkHttpClient.newCall(request).execute();
			if (response.isSuccessful()) {

				String resultString = response.body().string();
//				JSONObject jsonObject = new JSONObject(resultString);
//				jsonObject.get("");

				MaterialVoucher materialVoucher = mGson.fromJson(resultString, MaterialVoucher.class);


			}
		} catch (IOException e) {
			e.printStackTrace();
		}

		return null;
	}

}
