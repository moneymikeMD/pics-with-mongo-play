package controllers;

import com.mongo.MongoInstance;
import com.mongodb.DBCursor;
import com.mongodb.gridfs.GridFS;
import com.mongodb.gridfs.GridFSDBFile;
import com.mongodb.gridfs.GridFSInputFile;
import play.Logger;
import play.mvc.Controller;
import play.mvc.Http;
import play.mvc.Result;
import views.html.index;
import views.html.picture;
import views.html.upload;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * Application class. File uploading code borrowed from Play Documentation
 */
public class Application extends Controller {

  private final static GridFS imageStore = new GridFS(MongoInstance.getInstance(), "images");

  public static Result index() {
    return ok(index.render("Your new application is ready."));
  }

  public static Result displayPictures() {
      List<String> pictureUrls = getImageLocations();
      return ok(picture.render(pictureUrls));
  }

  public static Result upload() {
      Http.MultipartFormData body = request().body().asMultipartFormData();
      Http.MultipartFormData.FilePart picture = body.getFile("picture");
      if (picture != null) {
          String fileName = picture.getFilename();
          String contentType = picture.getContentType();
          File file = picture.getFile();
          try {
              Logger.info("File: " + fileName + " Content Type: " + contentType +
                            "File Key: " + picture.getKey());
              GridFSInputFile savedPicture = imageStore.createFile(file);
              savedPicture.setFilename(fileName);
              savedPicture.save();

          } catch (IOException e) {
              Logger.error("Could not upload image: " + fileName + e);
          }
          return displayPictures();
      } else {
          flash("error", "Missing file");
          return redirect(routes.Application.index());
      }
  }

  public static Result displayUpload(){
      return ok(upload.render());
  }

  public static Result getImage(String filename){
      GridFSDBFile file = imageStore.findOne(filename);
      if(file != null) {
        InputStream image = file.getInputStream();
        return ok(image);
      }
      else {
          return badRequest("Not Found");
      }
  }

  public static Result removePicture(String filename){
      imageStore.remove(filename);
      return displayPictures();
  }

  private static List<String> getImageLocations(){
      List<String> fileList = new ArrayList<String>();
      DBCursor cursor = imageStore.getFileList();
      while(cursor.hasNext()){
              fileList.add(
                  ((GridFSDBFile) cursor.next())
                          .getFilename()
              );
      }
      return fileList;
  }
}