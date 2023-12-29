package com.example.fisherydashboard;

import com.example.fisherydashboard.dto.CountrySetting;
import com.example.fisherydashboard.dto.HarvestedFishRecords;
import com.example.fisherydashboard.dto.Members;
import com.example.fisherydashboard.dto.TypeOfFish;
import com.example.fisherydashboard.enums.Country;
import com.example.fisherydashboard.enums.Region;
import com.example.fisherydashboard.service.RestApiCallServiceImpl;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.chart.PieChart;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.input.MouseEvent;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import java.time.LocalDateTime;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

import static com.example.fisherydashboard.Helper.RESTApiRequestURL.*;

public class HelloController {
    @FXML
    private ListView lvCountry;

    @FXML
    private ListView lvTop5Members;

    @FXML
    private Label lblCountryTitleText;

    @FXML
    private Label lblC1NE;

    @FXML
    private Label lblC1NW;

    @FXML
    private Label lblC1SE;

    @FXML
    private Label lblC1SW;

    @FXML
    private Label lblC3NE;

    @FXML
    private Label lblC3NW;

    @FXML
    private Label lblC3SE;

    @FXML
    private Label lblC3SW;

    @FXML
    private Label lblC5NE;

    @FXML
    private Label lblC5NW;

    @FXML
    private Label lblC5SE;

    @FXML
    private Label lblC5SW;

    @FXML
    private Label lblC3FishName;

    @FXML
    private Label lblC4TotalWeightTH;

    @FXML
    private PieChart pieChart;

    RestApiCallServiceImpl restApiCallService = new RestApiCallServiceImpl();
    List<TypeOfFish> typeOfFishDataList = new ArrayList<>();
    List<HarvestedFishRecords> harvestedFishRecordDataList = new ArrayList<>();
    List<CountrySetting> countrySettingDataList = new ArrayList<>();
    List<Members> membersDataList = new ArrayList<>();

    @FXML
    public void initialize() {
        loadRestAPIData();
        initListViewData();
        lvCountry.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                CountrySetting countrySetting = (CountrySetting) lvCountry.getSelectionModel().getSelectedItem();
                System.out.println("clicked on " + countrySetting.getCountry());
                initData(countrySetting);
            }
        });
    }

    private void initData(CountrySetting countrySetting) {
        Country country = countrySetting.getCountry();
        lblCountryTitleText.setText(country.name() + " " + countrySetting.getTotalWeightFished() + "KG");
        LocalDateTime local = LocalDateTime.now().minusYears(1);
        List<HarvestedFishRecords> currentCountryHarvest = harvestedFishRecordDataList.stream()
                .filter(f -> country == f.getCountry())
                .filter(f -> local.isBefore(f.getTimeLog()))
                .toList();

        Double NE = currentCountryHarvest.stream()
                .filter(f -> Region.NORTH_EAST == f.getRegion())
                .map(HarvestedFishRecords::getWeightKg)
                .reduce(0D, Double::sum);
        lblC1NE.setText(NE + "KG");
        Double NW = currentCountryHarvest.stream()
                .filter(f -> Region.NORTH_WEST == f.getRegion())
                .map(HarvestedFishRecords::getWeightKg)
                .reduce(0D, Double::sum);
        lblC1NW.setText(NW + "KG");

        Double SE = currentCountryHarvest.stream()
                .filter(f -> Region.SOUTH_EAST == f.getRegion())
                .map(HarvestedFishRecords::getWeightKg)
                .reduce(0D, Double::sum);
        lblC1SE.setText(SE + "KG");

        Double SW = currentCountryHarvest.stream()
                .filter(f -> Region.SOUTH_WEST == f.getRegion())
                .map(HarvestedFishRecords::getWeightKg)
                .reduce(0D, Double::sum);
        lblC1SW.setText(SW + "KG");

        Map<Long, Long> typeOfFishIdMap = currentCountryHarvest.stream()
                .map(HarvestedFishRecords::getTypeOfFishId)
                .collect(Collectors.groupingBy(Function.identity(), Collectors.counting()));

        ObservableList<PieChart.Data> pieChartData = FXCollections.observableArrayList();

        typeOfFishIdMap.forEach((typeOfFishId, count) -> typeOfFishDataList.stream().filter(f -> f.getTypeOfFishId().equals(typeOfFishId))
                .findFirst()
                .ifPresent(p -> pieChartData.add(new PieChart.Data(p.getTypeOfFishName(), count))));


        pieChart.setData(pieChartData);

        for(PieChart.Data data : pieChart.getData()) {
            data.getNode().addEventHandler(MouseEvent.MOUSE_CLICKED, new EventHandler<MouseEvent>() {
                @Override
                public void handle(MouseEvent event) {
                    handlePieChartClick(data.getName(), currentCountryHarvest);
                }
            });
        }

        OptionalDouble thAverage =
                Arrays.asList(countrySetting.getThreshHoldNorthEast(), countrySetting.getThreshHoldNorthWest(),
                        countrySetting.getThreshHoldSouthEast(), countrySetting.getThreshHoldSouthWest()).stream()
                        .mapToDouble(a -> a)
                        .average();

        LocalDateTime thAverageDateTime = LocalDateTime.now().minusDays((long) thAverage.getAsDouble());
        List<HarvestedFishRecords> thCurrentCountryHarvest = harvestedFishRecordDataList.stream()
                .filter(f -> country == f.getCountry())
                .filter(f -> thAverageDateTime.isBefore(f.getTimeLog()))
                .toList();

        Double totalWeightTH = thCurrentCountryHarvest.stream().map(HarvestedFishRecords::getWeightKg)
                .reduce(0D, Double::sum);
        lblC4TotalWeightTH.setText(totalWeightTH + "KG");

        Double NE_TH = thCurrentCountryHarvest.stream()
                .filter(f -> Region.NORTH_EAST == f.getRegion())
                .map(HarvestedFishRecords::getWeightKg)
                .reduce(0D, Double::sum);
        lblC5NE.setText(NE_TH + "KG");
        Double NW_TH = thCurrentCountryHarvest.stream()
                .filter(f -> Region.NORTH_WEST == f.getRegion())
                .map(HarvestedFishRecords::getWeightKg)
                .reduce(0D, Double::sum);
        lblC5NW.setText(NW_TH + "KG");

        Double SE_TH = thCurrentCountryHarvest.stream()
                .filter(f -> Region.SOUTH_EAST == f.getRegion())
                .map(HarvestedFishRecords::getWeightKg)
                .reduce(0D, Double::sum);
        lblC5SE.setText(SE_TH + "KG");

        Double SW_TH = thCurrentCountryHarvest.stream()
                .filter(f -> Region.SOUTH_WEST == f.getRegion())
                .map(HarvestedFishRecords::getWeightKg)
                .reduce(0D, Double::sum);
        lblC5SW.setText(SW_TH + "KG");

        Map<Long, DoubleSummaryStatistics> top5Members = currentCountryHarvest.stream()
                .collect(Collectors.groupingBy(HarvestedFishRecords::getMembersId,
                        Collectors.summarizingDouble(HarvestedFishRecords::getWeightKg)));

        List<Long> top5MembersId = top5Members.entrySet().stream()
                .sorted(Map.Entry.comparingByValue(Comparator.comparingDouble(DoubleSummaryStatistics::getSum)))
                .limit(5)
                .map(Map.Entry::getKey)
                .collect(Collectors.toList());
        Collections.reverse(top5MembersId);
        ArrayList<String> top5memberNames = new ArrayList<>();
        top5MembersId.forEach(fe -> membersDataList.stream()
                .filter(f -> f.getMemberId().equals(fe)).findFirst()
                .ifPresent(p -> top5memberNames.add(p.getFirstName() + " " + p.getLastName() + " - Total weight harvested: " + top5Members.get(fe).getSum())));
        lvTop5Members.getItems().addAll(top5memberNames);
    }

    private void handlePieChartClick(String name, List<HarvestedFishRecords> currentCountryHarvest) {
        typeOfFishDataList.stream().filter(f -> f.getTypeOfFishName().equals(name))
                .findFirst()
                .ifPresent(p -> {
                    lblC3FishName.setText(p.getTypeOfFishName());
                    Double NE = currentCountryHarvest.stream()
                            .filter(f -> Region.NORTH_EAST == f.getRegion())
                            .filter(f -> f.getTypeOfFishId().equals(p.getTypeOfFishId()))
                            .map(HarvestedFishRecords::getWeightKg)
                            .reduce(0D, Double::sum);
                    lblC3NE.setText(NE + "KG");
                    Double NW = currentCountryHarvest.stream()
                            .filter(f -> Region.NORTH_WEST == f.getRegion())
                            .filter(f -> f.getTypeOfFishId().equals(p.getTypeOfFishId()))
                            .map(HarvestedFishRecords::getWeightKg)
                            .reduce(0D, Double::sum);
                    lblC3NW.setText(NW + "KG");

                    Double SE = currentCountryHarvest.stream()
                            .filter(f -> Region.SOUTH_EAST == f.getRegion())
                            .filter(f -> f.getTypeOfFishId().equals(p.getTypeOfFishId()))
                            .map(HarvestedFishRecords::getWeightKg)
                            .reduce(0D, Double::sum);
                    lblC3SE.setText(SE + "KG");

                    Double SW = currentCountryHarvest.stream()
                            .filter(f -> Region.SOUTH_WEST == f.getRegion())
                            .filter(f -> f.getTypeOfFishId().equals(p.getTypeOfFishId()))
                            .map(HarvestedFishRecords::getWeightKg)
                            .reduce(0D, Double::sum);
                    lblC3SW.setText(SW + "KG");
                });
    }

    private void initListViewData() {

        lvCountry.getItems().addAll(countrySettingDataList);
        lvCountry.setCellFactory(param -> new ListCell<CountrySetting>() {
            @Override
            protected void updateItem(CountrySetting item, boolean empty) {
                super.updateItem(item, empty);

                if (empty || item == null || item.getCountry() == null) {
                    setText(null);
                } else {
                    setText(item.getCountry() + " " + item.getTotalWeightFished() + "KG");
                }
            }
        });
    }

    private void loadRestAPIData() {

        try {
            JSONObject jsonObjectHarvestedFishRecords = restApiCallService.sendGetRequest(GET_HARVESTEDFISHRECORDS, null);
            JSONObject jsonObjectTypeOfFish = restApiCallService.sendGetRequest(GET_TYPEOFFISHLIST, null);
            JSONObject jsonObjectMembers = restApiCallService.sendGetRequest(GET_MEMBERS, null);
            JSONObject jsonObjectCountrySettings = restApiCallService.sendGetRequest(GET_COUNTRYSETTINGS, null);

            String jsonObjectHarvestedFishRecordsResponseCode = jsonObjectHarvestedFishRecords.get("code").toString();
            String jsonObjectTypeOfFishResponseCode = jsonObjectHarvestedFishRecords.get("code").toString();
            String jsonObjectMembersResponseCode = jsonObjectHarvestedFishRecords.get("code").toString();
            String jsonObjectCountrySettingsResponseCode = jsonObjectHarvestedFishRecords.get("code").toString();

            if (Objects.isNull(jsonObjectHarvestedFishRecords) || Objects.isNull(jsonObjectTypeOfFish) || Objects.isNull(jsonObjectMembers)
                    || !"200".equals(jsonObjectHarvestedFishRecordsResponseCode) || !"200".equals(jsonObjectTypeOfFishResponseCode)
                    || !"200".equals(jsonObjectMembersResponseCode) || !"200".equals(jsonObjectCountrySettingsResponseCode)) {

            } else {
                JSONParser parser = new JSONParser();
                org.json.simple.JSONArray jsonArray;
                jsonArray = (org.json.simple.JSONArray) parser.parse(jsonObjectTypeOfFish.get("body").toString());

                    jsonArray.forEach(tof -> {

                        JSONObject tofObject = (JSONObject) tof;

                        TypeOfFish typeOfFish = new TypeOfFish();
                        typeOfFish.setTypeOfFishId(Long.valueOf(tofObject.get("typeOfFishId").toString()));
                        typeOfFish.setActive(Boolean.valueOf(tofObject.get("active").toString()));
                        typeOfFish.setTypeOfFishName(tofObject.get("typeOfFishName").toString());

                        byte[] decodedString = Base64.getDecoder().decode(tofObject.get("typeOfFishPictureBase64").toString());
                        typeOfFish.setTypeOfFishPictureBase64(decodedString);
                        typeOfFishDataList.add(typeOfFish);
                    });

                jsonArray = (org.json.simple.JSONArray) parser.parse(jsonObjectHarvestedFishRecords.get("body").toString());

                jsonArray.forEach(tof -> {

                    JSONObject tofObject = (JSONObject) tof;

                    HarvestedFishRecords hfr = new HarvestedFishRecords();
                    hfr.setFisheringId(Long.valueOf(tofObject.get("fisheringId").toString()));
                    JSONObject typeOfFishJsonObject = (JSONObject) tofObject.get("typeOfFish");
                    hfr.setMembersId(Long.valueOf(tofObject.get("membersId").toString()));
                    hfr.setTypeOfFishId(Long.valueOf(typeOfFishJsonObject.get("typeOfFishId").toString()));
                    hfr.setRegion(Region.valueOf(tofObject.get("region").toString()));
                    hfr.setWeightKg(Double.valueOf(tofObject.get("weightKg").toString()));
                    hfr.setCountry(Country.valueOf(tofObject.get("country").toString()));
                    hfr.setTimeLog(LocalDateTime.parse(tofObject.get("timeLog").toString()));

                    harvestedFishRecordDataList.add(hfr);
                });


                jsonArray = (org.json.simple.JSONArray) parser.parse(jsonObjectMembers.get("body").toString());

                jsonArray.forEach(tof -> {

                    JSONObject tofObject = (JSONObject) tof;

                    Members member = new Members();
                    member.setMemberId(Long.valueOf(tofObject.get("memberId").toString()));
                    member.setFirstName(String.valueOf(tofObject.get("firstName").toString()));
                    member.setLastName(String.valueOf(tofObject.get("lastName").toString()));
                    member.setUsername(String.valueOf(tofObject.get("username").toString()));
                    if (Objects.nonNull(tofObject.get("typeOfFishPictureBase64"))) {
                        byte[] decodedString = Base64.getDecoder().decode(tofObject.get("typeOfFishPictureBase64").toString());
                        member.setProfilePictureBase64(decodedString);
                    }
                    membersDataList.add(member);
                });

                jsonArray = (org.json.simple.JSONArray) parser.parse(jsonObjectCountrySettings.get("body").toString());

                jsonArray.forEach(tof -> {

                    JSONObject tofObject = (JSONObject) tof;

                    CountrySetting countrySetting = new CountrySetting();
                    countrySetting.setCountry(Country.valueOf(String.valueOf(tofObject.get("country").toString())));
                    countrySetting.setThreshHoldNorthWest(Double.valueOf(tofObject.get("threshHoldNorthWest").toString()));
                    countrySetting.setThreshHoldSouthWest(Double.valueOf(tofObject.get("threshHoldSouthWest").toString()));
                    countrySetting.setThreshHoldNorthEast(Double.valueOf(tofObject.get("threshHoldNorthEast").toString()));
                    countrySetting.setThreshHoldSouthEast(Double.valueOf(tofObject.get("threshHoldSouthEast").toString()));
                    Double totalWeightFished = harvestedFishRecordDataList.stream()
                            .filter(f -> countrySetting.getCountry() == f.getCountry())
                            .map(HarvestedFishRecords::getWeightKg)
                            .reduce(0D, Double::sum);
                    countrySetting.setTotalWeightFished(totalWeightFished);
                    countrySettingDataList.add(countrySetting);
                });
                countrySettingDataList.stream().sorted();
                Collections.reverse(countrySettingDataList);
            }
        } catch (Exception ignore) {
            ignore.printStackTrace();
        }
    }
}