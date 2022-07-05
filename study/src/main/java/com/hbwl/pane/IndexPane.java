package com.hbwl.pane;

import com.hbwl.model.Type;
import com.hbwl.parse.NovelParse;
import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

import java.io.IOException;

public class IndexPane extends Application {
    private Button search;
    private ComboBox option;
    private TextField keyword;

    @Override
    public void start(Stage primaryState) throws Exception {
        // 加载第一个面板
        Pane indexPane = (Pane)FXMLLoader.load(IndexPane.class.getClassLoader().getResource("index.fxml"));

        // 绑定
        option = (ComboBox) indexPane.lookup("#option");
        keyword = (TextField) indexPane.lookup("#keyword");
        search = (Button) indexPane.lookup("#search");
        option.getItems().add(new Type("title", "标题"));
        option.getItems().add(new Type("author", "作者"));
        option.getItems().add(new Type("fictionType", "分类"));
        // 默认选中第一个
        option.getSelectionModel().select(0);

        // 给控件绑定一个事件
        search.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                // 获取到你选中的类型
                Type selectedItem =(Type) option.getSelectionModel().getSelectedItem();
                //获取输入的关键字
                String kwvalue=keyword.getText();

                // 切换场景
                try {
                    // 加载第二个面板
                    ScrollPane listPane = (ScrollPane)FXMLLoader.load(IndexPane.class.getClassLoader().getResource("list.fxml"));
                    NovelParse.getBookList(selectedItem.getCode(), kwvalue, 10, listPane);

                    Scene scene2 =  new Scene(listPane);
                    primaryState.setScene(scene2);

                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        });

        // 把面板放到场景里去
        Scene scene =  new Scene(indexPane);
        // 把场景放到舞台
        primaryState.setScene(scene);
        // 给舞台设置一个标题
        primaryState.setTitle("希希里的图书馆典藏");
        primaryState.setResizable(false);
        // 把舞台展示出来
        primaryState.show();
    }

    public static void main(String[] args) {
        // Application.launch(XXX.class);
        launch(args);
    }
}