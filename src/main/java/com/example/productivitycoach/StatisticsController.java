package com.example.productivitycoach;

import com.example.productivitycoach.model.User;
import com.example.productivitycoach.services.StatsService;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.chart.*;
import javafx.scene.control.Label;
import java.util.Map;

public class StatisticsController {

    @FXML private Label labelTotalFocus, labelCompleted, labelSuccess, labelCategory;
    @FXML private PieChart pieChartTime;
    @FXML private BarChart<String, Number> barChartProductivity;
    @FXML private CategoryAxis xAxis;
    @FXML private NumberAxis yAxis;

    private StatsService statsService = new StatsService();
    private int currentUserId = 1; // single declaration — updated by setCurrentUser()

    @FXML
    public void initialize() {
        // initialize() runs before setCurrentUser() is called,
        // so we only load data here if currentUserId is already set.
        // The actual load is triggered by setCurrentUser() instead.
    }

    public void setCurrentUser(User user) {
        this.currentUserId = user.getId();
        loadSummaryCards();
        loadPieChart();
        loadBarChart();
    }

    private void loadSummaryCards() {
        labelTotalFocus.setText(statsService.getTotalMinutes(currentUserId) + " min");
        labelCompleted.setText(String.valueOf(statsService.getCompletedTasksCount(currentUserId)));
        labelSuccess.setText(statsService.getSuccessRate(currentUserId) + "%");
        labelCategory.setText(statsService.getDominantCategory(currentUserId));
    }

    private void loadPieChart() {
        ObservableList<PieChart.Data> pieData = FXCollections.observableArrayList();
        Map<String, Integer> data = statsService.getTimePerCategory(currentUserId);
        data.forEach((cat, time) -> pieData.add(new PieChart.Data(cat, time)));
        pieChartTime.setData(pieData);
    }

    private void loadBarChart() {
        XYChart.Series<String, Number> series = new XYChart.Series<>();
        series.setName("Tâches terminées");
        Map<String, Integer> weeklyData = statsService.getWeeklyProductivity(currentUserId);
        weeklyData.forEach((day, count) -> series.getData().add(new XYChart.Data<>(day, count)));
        barChartProductivity.getData().add(series);
    }
}