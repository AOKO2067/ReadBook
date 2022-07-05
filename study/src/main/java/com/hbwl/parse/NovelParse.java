package com.hbwl.parse;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.hbwl.model.ListViewModel;
import com.hbwl.pane.IndexPane;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import org.jsoup.Jsoup;

import java.io.BufferedInputStream;
import java.io.IOException;

public class NovelParse {
    public static void main(String[] args) {
        // getBookList("title", "斗罗大陆", 5);

    }

    // 获取章节对应的文章
    public static void getChapterContent(String chapterId,SplitPane splitPane)
    {
        // splitPane不能拖动
        ObservableList<Node> items = splitPane.getItems();
        ScrollPane scrollPane = (ScrollPane) items.get(0);
        AnchorPane anchorPane = (AnchorPane) items.get(1);
        TextArea textArea = (TextArea) anchorPane.lookup("#content");
        ListView listView = (ListView) scrollPane.getContent().lookup("#chapterList");
        //当到了最后一个界面的时候 允许你的拖动
        Stage stage = (Stage)splitPane.getScene().getWindow();
        stage.setResizable(true);
        //监听舞台的拉伸的事件
        stage.widthProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observableValue, Number number, Number t1) {
                //设置左边为20%
                listView.setPrefWidth(stage.getWidth()*0.2);
                //设置右边为80%
                textArea.setPrefWidth(stage.getWidth()*0.8);
                //设置分割条永远按照20%，80%分割
                splitPane.setDividerPositions(0.2f,0.8f);
            }
        });

        stage.heightProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observableValue, Number number, Number t1) {
                listView.setPrefHeight(stage.getHeight());
                textArea.setPrefHeight(stage.getHeight());
            }
        });

        String request = request("https://api.pingcc.cn/fictionContent/search/" + chapterId);
        // System.out.println(request);
        JSONObject jsonObject = (JSONObject) JSONObject.parse(request);
        JSONArray data = jsonObject.getJSONArray("data");


        String content = "";
        for(int i=0; i<data.size(); i++)
        {
            content += data.getString(i) + "\r\n";
        }
//        Region region = (Region) textArea.lookup(".content");
//        region.setBackground(new Background(new BackgroundFill(Color.YELLOW, CornerRadii.EMPTY, Insets.EMPTY)));
        // region.setStyle("-fx-background-color: yellow");
        //自动换行
        textArea.setFont(new Font(15));
        textArea.setWrapText(true);
        textArea.setText(content);
    }
//    public static void getChapterContent(String chapterId, SplitPane splitPane) {
//        String request = request("https://api.pingcc.cn/fictionContent/search/" + chapterId);
//        // System.out.println(request);
//        JSONObject jsonObject = (JSONObject) JSONObject.parse(request);
//        JSONArray data = jsonObject.getJSONArray("data");
//        ObservableList<Node> items = splitPane.getItems();
//        AnchorPane anchorPane = (AnchorPane) items.get(1);
//        TextArea textArea = (TextArea) anchorPane.lookup("#content");
//        String content = "";
//        for (int i=0; i<data.size(); i++){
//            // System.out.println(data.getString(i));
//            // System.out.println("\n");
//            content += data.getString(i) + "\r\n";
//        }
//        textArea.setFont(new Font(14));
//        textArea.setWrapText(true);
//        textArea.setText(content);
//    }

    // 获取图书的所有章节
    public static void getChapterList(String fictionId, SplitPane pane) {
        String request = request("https://api.pingcc.cn/fictionChapter/search/" + fictionId);
        // System.out.println(request);
        JSONObject jsonObject = (JSONObject) JSONObject.parse(request);
        JSONObject data = jsonObject.getJSONObject("data");
        JSONArray chapterList = data.getJSONArray("chapterList");
        ObservableList<Node> items = pane.getItems();
        ScrollPane pane1 = (ScrollPane) items.get(0);
        AnchorPane anchorPane = (AnchorPane) pane1.getContent();
        // 获取到控件
        ListView listView = (ListView) anchorPane.lookup("#chapterList");

        for (int i=0; i<chapterList.size(); i++){
            JSONObject chapter = chapterList.getJSONObject(i);
            // System.out.println(chapter.getString("title"));
            // System.out.println("章节ID：" + chapter.getString("chapterId"));
            // System.out.println("\n");
            String title = chapter.getString("title");
            String chapterId = chapter.getString("chapterId");
//            System.out.println(title);
//            System.out.println(chapterId);
            listView.getItems().add(new ListViewModel(chapterId, title));
            // listView.getItems().add(new ListViewModel(chapter.getString("chapterId"), chapter.getString("title")));
            // listView.getItems().add(chapter.getString("title"));
            // fx:id="chapterList"
        }
        // 创建点击事件
        listView.getSelectionModel().getSelectedItems().addListener(new ListChangeListener() {
            @Override
            public void onChanged(Change change) {
//                System.out.println("我被点击了");
                // 获取被选中的对象
                ListViewModel selectedItems = (ListViewModel) listView.getSelectionModel().getSelectedItem();
//                System.out.println(selectedItems.getCode());
//                System.out.println(selectedItems.getTitle());
                getChapterContent(selectedItems.getCode(), pane);
            }
        });
    }

    // 获取所有搜索图书的列表
    public static void getBookList(String searchTypes, String keyword, int size, ScrollPane scrollPane) {
        // https://api.pingcc.cn/fiction/search/{option}/{key}/{from}/{size}
        String request = request("https://api.pingcc.cn/fiction/search/" + searchTypes + "/" + keyword + "/1/" + size);
        // System.out.println(request);
        JSONObject jsonObject = (JSONObject) JSONObject.parse(request);
        JSONArray jsonArray = jsonObject.getJSONArray("data");

        // JSONObject novel = (JSONObject) jsonArray.get(0);
        AnchorPane pane = (AnchorPane) scrollPane.getContent();
        pane.setPrefHeight(32 +134 * jsonArray.size() + 100); // 5->50 10->100为变量

//        // 获取界面上所有的控件
//        ImageView cover = (ImageView) pane.lookup("#cover");
//        // System.out.println(cover);
//        Label author = (Label) pane.lookup("#author");
//        Label fictionType = (Label) pane.lookup("#fictionType");
//        Label title = (Label) pane.lookup("#title");
//        // 给控件设置数据
//        cover.setImage(new Image(novel.getString("cover")));
//        author.setText(novel.getString("author"));
//        fictionType.setText(novel.getString("fictionType"));
//        title.setText(novel.getString("title"));

        for (int i=0; i<jsonArray.size(); i++){
            JSONObject novel = (JSONObject) jsonArray.get(i);

            // 添加图片
            ImageView iv = new ImageView();
            iv.setX(33);
            iv.setY(32 + 134*i + 10*(i-1));
            iv.setFitWidth(96);
            iv.setFitHeight(134);
            iv.setImage(new Image(requestImage(novel.getString("cover"))));
            pane.getChildren().add(iv);

            // 添加标题
            Label title = new Label();
            title.setText(novel.getString("title"));
            title.setLayoutX(155);
            title.setLayoutY(42 + 134*i + 10*(i-1));
            title.setFont(new Font(20));
            pane.getChildren().add(title);

            // 添加作者
            Label author = new Label();
            author.setText(novel.getString("author"));
            author.setLayoutX(170);
            author.setLayoutY(87 + 134*i + 10*(i-1));
            author.setFont(new Font(13));
            pane.getChildren().add(author);

            // 添加类型
            Label fictionType = new Label();
            fictionType.setText(novel.getString("fictionType"));
            fictionType.setLayoutX(170);
            fictionType.setLayoutY(111 + 134*i + 10*(i-1));
            fictionType.setFont(new Font(13));
            pane.getChildren().add(fictionType);

            // 添加简介
            Label descs = new Label();
            descs.setLayoutX(271);
            descs.setLayoutY(70 + 134*i + 10*(i-1));
            descs.setPrefWidth(278);
            descs.setPrefHeight(100);
            descs.setFont(new Font(13));
            // descs.setAlignment(JUSTIFY);
            descs.setWrapText(true);
            // textAlignment="JUSTIFY" wrapText="true"
            descs.setText(novel.getString("descs"));
            pane.getChildren().add(descs);

            // 添加阅读按钮
            Button read = new Button();
            read.setLayoutX(165);
            read.setLayoutY(137 + 134*i + 10*(i-1));
//            read.setPrefWidth();
//            read.setPrefHeight();
            read.setFont(new Font(14));
            read.setText("阅读");
            // 区分每一个按钮
            read.setId(novel.getString("fictionId"));
            pane.getChildren().add(read);

            // 监听按钮的点击事件 给每个按钮添加一个点击事件
            read.setOnAction(new EventHandler<ActionEvent>() {
                @Override
                public void handle(ActionEvent actionEvent) {
                    Button source = (Button) actionEvent.getSource();
//                    System.out.println(source.getId());
//                    System.out.println(source.getParent());
//                    System.out.println(((AnchorPane) source.getParent()).getParent());
//                    System.out.println(((AnchorPane) source.getParent()).getParent().getScene());
//                    System.out.println(((AnchorPane) source.getParent()).getParent().getScene().getWindow());
                    Stage stage = (Stage) ((AnchorPane) source.getParent()).getParent().getScene().getWindow();

                    // 加载第三个面板
                    try {
                        SplitPane splitPane = (SplitPane) FXMLLoader.load(IndexPane.class.getClassLoader().getResource("read.fxml"));
                        getChapterList(source.getId(), splitPane);
                        Scene scene3 = new Scene(splitPane);
                        stage.setScene(scene3);
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                }
            });

//            JSONObject novel = jsonArray.getJSONObject(i);
//            System.out.println("小说ID：" + novel.getString("fictionId"));
//            System.out.println("标题：" + novel.getString("title"));
//            System.out.println("作者：" + novel.getString("author"));
//            System.out.println("内容简介：" + novel.getString("descs"));
//            System.out.println("封面图片：" + novel.getString("cover"));
//            System.out.println("更新时间：" + novel.getString("updateTime"));
//            System.out.println("\n");
        }
    }
    // 封装一个请求的方法
    public static String request(String url){
        try {
            return Jsoup.connect(url).ignoreContentType(true)
                    .maxBodySize(20*1024*1024).execute().body();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static BufferedInputStream requestImage(String url){
        try {
            return Jsoup.connect(url).ignoreContentType(true)
                    .maxBodySize(20*1024*1024).execute().bodyStream();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
