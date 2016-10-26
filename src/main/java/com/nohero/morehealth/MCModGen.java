package com.nohero.morehealth;

import com.google.gson.stream.JsonWriter;
import java.io.File;
import java.io.IOException;
import java.io.StringWriter;
import org.apache.commons.io.FileUtils;

public class MCModGen
{
  public static void main(String[] args)
    throws IOException
  {
    StringWriter stringWriter = new StringWriter();
    JsonWriter jsonWriter = new JsonWriter(stringWriter);
    
    jsonWriter.beginArray();
    
    jsonWriter.beginObject();
    jsonWriter.name("modid");
    jsonWriter.value("morehealth");
    
    jsonWriter.name("name");
    jsonWriter.value("More Health Forge");
    
    jsonWriter.name("description");
    jsonWriter.value("More Health Mod");
    
    jsonWriter.name("version");
    jsonWriter.value("6.5 BETA");
    
    jsonWriter.name("mcversion");
    jsonWriter.value("");
    
    jsonWriter.name("url");
    jsonWriter.value("www.moddednetwork.com");
    
    jsonWriter.name("updateUrl");
    jsonWriter.value("");
    
    jsonWriter.name("authorList");
    jsonWriter.beginArray();
    jsonWriter.value("nohero, runescapejon");
    jsonWriter.endArray();
    
    jsonWriter.name("credits");
    jsonWriter.value("Created by nohero");
    
    jsonWriter.name("logoFile");
    jsonWriter.value("");
    
    jsonWriter.name("screenshots");
    jsonWriter.beginArray();
    jsonWriter.endArray();
    
    jsonWriter.name("dependencies");
    jsonWriter.beginArray();
    jsonWriter.endArray();
    
    jsonWriter.endObject();
    jsonWriter.endArray();
    
    jsonWriter.flush();
    
    String json = stringWriter.toString();
    jsonWriter.close();
    
    FileUtils.writeStringToFile(new File("mcmod.info"), json);
  }
}
