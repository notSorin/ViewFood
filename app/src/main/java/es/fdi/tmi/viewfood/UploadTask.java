package es.fdi.tmi.viewfood;

import android.os.AsyncTask;
import android.util.Log;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.File;
import java.io.IOException;
import java.util.concurrent.TimeUnit;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class UploadTask extends AsyncTask<String, String, String>
{
    private final String SERVER_URL = "http://35.246.247.149/api/menu/menu/upload/";
    private MainActivity _mainActivity;

    public UploadTask(MainActivity ma)
    {
        _mainActivity = ma;
    }

    @Override
    protected String doInBackground(String... strings)
    {
        uploadFile(strings[0], strings[1]);

        return "task ended";
    }

    private void uploadFile(String path, String targetTranslation)
    {
        File f = new File(path);
        RequestBody requestBody = new MultipartBody.Builder().setType(MultipartBody.FORM).
                addFormDataPart("image", f.getName(), RequestBody.create(MediaType.parse("image/*"), f)).
                addFormDataPart("lang", targetTranslation).
                build();

        try
        {
            Log.d("UPLOAD_FILE", "Request size is " + requestBody.contentLength());
        }
        catch(IOException e)
        {
            e.printStackTrace();
        }

        Request request = new Request.Builder().url(SERVER_URL).post(requestBody).build();
        OkHttpClient client = new OkHttpClient.Builder()
                .connectTimeout(10, TimeUnit.SECONDS)
                .writeTimeout(10, TimeUnit.SECONDS)
                .readTimeout(10, TimeUnit.SECONDS)
                .build();

        client.newCall(request).enqueue(new Callback()
        {
            @Override
            public void onFailure(Call call, IOException e)
            {
                _mainActivity.runOnUiThread(() -> _mainActivity.displayAlert("A network error has occurred. Please check your internet connection and try again"));
            }

            @Override
            public void onResponse(Call call, Response response)
            {
                try
                {
                    JSONObject jObject = new JSONObject(response.body().string());

                    _mainActivity.runOnUiThread(() ->
                    {
                        try
                        {
                            _mainActivity.setResponseFromServer(jObject.getString("description"), jObject.getString("image"));
                        }
                        catch(JSONException e)
                        {
                            e.printStackTrace();
                        }
                    });
                }
                catch(JSONException | IOException e)
                {
                    e.printStackTrace();
                }
            }
        });
    }
}