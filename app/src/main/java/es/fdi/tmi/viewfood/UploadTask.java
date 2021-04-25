package es.fdi.tmi.viewfood;

import android.os.AsyncTask;
import android.util.Log;
import java.io.File;
import java.io.IOException;
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
    private final String SERVER_URL = "http://35.246.247.149/api/menu/menu/";

    @Override
    protected String doInBackground(String... strings)
    {
        uploadFile(strings[0], strings[0]);

        return "task ended";
    }

    private void uploadFile(String path, String targetTranslation)
    {
        File f = new File(path);
        RequestBody requestBody = new MultipartBody.Builder().setType(MultipartBody.FORM).
                addFormDataPart("image", f.getName(), RequestBody.create(MediaType.parse("image/*"), f)).
                addFormDataPart("lang", targetTranslation).
                build();

        //TODO Delete debug logs for release.
        try
        {
            Log.d("UPLOAD_FILE", "Request size is " + requestBody.contentLength());
        }
        catch(IOException e)
        {
            e.printStackTrace();
        }

        Request request = new Request.Builder().url(SERVER_URL).post(requestBody).build();
        OkHttpClient client = new OkHttpClient();

        client.newCall(request).enqueue(new Callback()
        {
            @Override
            public void onFailure(Call call, IOException e)
            {
                Log.d("UPLOAD_FILE", e.toString());
            }

            @Override
            public void onResponse(Call call, Response response)
            {
                Log.d("UPLOAD_FILE", response.toString());
            }
        });
    }
}
