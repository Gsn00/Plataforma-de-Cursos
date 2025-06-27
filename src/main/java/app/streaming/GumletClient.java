package app.streaming;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

@Service
public class GumletClient {

	private OkHttpClient okHttp;
	
	@Value("${gumlet.api-key}") 
	private String apiKey;
	
	public GumletClient() {
		this.okHttp = new OkHttpClient();
	}
	
	@SuppressWarnings("unchecked")
	public Map<String, String> requestUploadInfo(String title, String description) {
		try {
			MediaType mediaType = MediaType.parse("application/json");
			
			RequestBody body = RequestBody.create("{\"format\":\"MP4\",\"collection_id\":\"685b0700da5f39a3fc00a0c1\",\"title\":\""+title+"\",\"description\":\""+description+"\"}", mediaType);
			Request request = new Request.Builder()
					.url("https://api.gumlet.com/v1/video/assets/upload")
					.post(body)
					.addHeader("accept", "application/json")
					.addHeader("content-type", "application/json")
					.addHeader("Authorization", "Bearer " + apiKey)
					.build();
		
			Response response = okHttp.newCall(request).execute();
			ObjectMapper objectMapper = new ObjectMapper();
			Map<String, Object> responseMap = objectMapper.readValue(response.body().string(), new TypeReference<Map<String, Object>>() {});
			Map<String, String> output = (Map<String, String>) responseMap.get("output");
			
			Map<String, String> map = new HashMap<>();
			map.put("asset_id", responseMap.get("asset_id").toString());
			map.put("playback_url", output.get("playback_url"));
			
			return map;
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public void uploadVideoToGumlet(String assetId, MultipartFile videoFile) {
		try {
			int sizeOfEachPart = 5 * 1024 * 1024;
			List<byte[]> parts = new ArrayList<>();
			InputStream inputStream = videoFile.getInputStream();
			byte[] buffer = new byte[sizeOfEachPart];
			int bytesRead;
			
			while ((bytesRead = inputStream.read(buffer)) != -1) {
				if (bytesRead < sizeOfEachPart) {
					byte[] partialBuffer = new byte[bytesRead];
					System.arraycopy(buffer, 0, partialBuffer, 0, bytesRead);
					parts.add(partialBuffer);
				} else {
					parts.add(buffer.clone());
				}
			}
			
			List<Map<String, Object>> eTags = new ArrayList<>();
			ObjectMapper objectMapper = new ObjectMapper();
			
			Request request;
			Response response;
			
			for (int i=0; i<parts.size(); i++) {
				request = new Request.Builder()
						.url("https://api.gumlet.com/v1/video/assets/"+assetId+"/multipartupload/"+ (i+1) +"/sign")
						.get()
						.addHeader("accept", "application/json")
						.addHeader("Authorization", "Bearer " + apiKey)
						.build();
				
				response = okHttp.newCall(request).execute();
				
				Map<String, Object> map = objectMapper.readValue(response.body().string(), new TypeReference<Map<String, Object>>() {});
				
				String partUrl = (String) map.get("part_upload_url");
				
				RequestBody body = RequestBody.create(parts.get(i), MediaType.parse("application/octet-stream"));
				
				request = new Request.Builder()
						.url(partUrl)
						.put(body)
						.build();
				
				response = okHttp.newCall(request).execute();
				System.out.println("ETags: "+response.header("ETag"));
				Map<String, Object> eTag = new HashMap<>();
				eTag.put("PartNumber", i + 1);
				eTag.put("ETag", response.header("ETag"));
				eTags.add(eTag);
			}
			
			String json = objectMapper.writeValueAsString(Map.of("parts", eTags));
			RequestBody jsonBody = RequestBody.create(json, MediaType.parse("application/json"));
			
			request = new Request.Builder()
				    .url("https://api.gumlet.com/v1/video/assets/" + assetId + "/multipartupload/complete")
				    .post(jsonBody)
				    .addHeader("Authorization", "Bearer " + apiKey)
				    .addHeader("accept", "application/json")
				    .addHeader("content-type", "application/json")
				    .build();

			response = okHttp.newCall(request).execute();
			if (!response.isSuccessful()) {
				throw new RuntimeException("Complete falhou: " + response.code() + " / " + response.body().string());
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
