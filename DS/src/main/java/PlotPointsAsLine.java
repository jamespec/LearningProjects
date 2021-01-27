import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;

import java.io.*;
import java.util.ArrayList;
import java.util.List;


public class PlotPointsAsLine extends Application
{
    LineChart<Number,Number> lineChart=null;
    ArrayList<XYChart.Series<Number, Number>> seriesList = null;

    @Override
    public void init() throws Exception {
        super.init();
        loadData();
    }

    void loadData()
    {
        List<String> p = getParameters().getRaw();

        if( p.size() > 0 ) {
            seriesList = new ArrayList<>();

            try(BufferedReader br = new BufferedReader(new FileReader(p.get(0)) )) {
                String line = br.readLine();
                if( line == null )
                    return;

                String[] headers = line.split("\t");

                for( String header : headers ) {
                    XYChart.Series<Number, Number> series = new XYChart.Series<>();
                    series.setName(header);
                    seriesList.add(series);
                }

                long lineNum = 1;
                while( (line=br.readLine()) != null ) {
                    String[] cols = line.split("\t");
                    for (int i = 0; i < cols.length; i++) {
                        double value = Double.parseDouble(cols[i]);
                        seriesList.get(i).getData().add(new XYChart.Data<>(lineNum, value));
                    }
                    lineNum++;
                }
            }
            catch(Exception e) {}
        }
    }

    @Override
    public void start(Stage stage)
    {
        stage.setTitle("Performance Timings");
        NumberAxis xAxis = new NumberAxis();
        xAxis.setLabel("Number of elements");
        NumberAxis yAxis = new NumberAxis();
        yAxis.setLabel("usecs per call");

        lineChart = new LineChart<Number,Number>(xAxis,yAxis);
        lineChart.setTitle("Performance Timings (usecs/call)");
        lineChart.setCreateSymbols(false);

        Scene scene  = new Scene(lineChart,800,600);
        for(int i=0; i<seriesList.size(); i++)
            lineChart.getData().add(seriesList.get(i));

        stage.setScene(scene);

        stage.addEventHandler(MouseEvent.MOUSE_CLICKED,
                new EventHandler<MouseEvent>() {
                    @Override
                    public void handle(MouseEvent t) {
                        if (t.getClickCount() > 0) {
                            loadData();
                            for(int i=0; i<seriesList.size(); i++)
                                lineChart.getData().set(i, seriesList.get(i));
                        }
                    }
                });
        stage.show();
    }


    public static void main(String[] args) {
        launch(args);
    }
}