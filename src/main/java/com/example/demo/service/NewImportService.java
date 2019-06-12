package com.example.demo.service;

import com.example.demo.model.ExcleColumn;
import com.example.demo.model.Field;
import com.example.demo.util.PinyinUtil;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 新的导入方法
 *
 * @author Chen
 * @create 2019 05 22 15:00
 **/
@Service
public class NewImportService {



    /**
     * 处理上传的文件
     * 处理空白单元格
     * @param in
     * @param fileName
     * @return
     * @throws Exception
     */
    public List getListByExcelWithBlank(InputStream in, String fileName) throws Exception {
        List list = new ArrayList<>();
        //创建Excel工作薄
        Workbook work = this.getWorkbook(in, fileName);
        if (null == work) {
            throw new Exception("创建Excel工作薄为空！");
        }
        Sheet sheet = null;
        Row row = null;
        Cell cell = null;

        for (int i = 0; i < work.getNumberOfSheets(); i++) {
            sheet = work.getSheetAt(i);
            if (sheet == null) {
                continue;
            }

            for (int j = sheet.getFirstRowNum(); j <= sheet.getLastRowNum(); j++) {
                row = sheet.getRow(j);
                if (row == null || row.getFirstCellNum() == j) {
                    continue;
                }
                int lastIndexOfColumn = Math.max(row.getLastCellNum(), 11);
                List<Object> li = new ArrayList<>();
                for (int y = row.getFirstCellNum(); y < lastIndexOfColumn; y++) {
                    cell = row.getCell(y,Row.MissingCellPolicy.CREATE_NULL_AS_BLANK);
                    if (cell == null) {

                    }else{

                    }
                    li.add(cell);
                }
                list.add(li);
            }
        }
        work.close();
        return list;
    }

    /**
     * 处理上传的Excle
     *  不处理空白或缺失单元格
     * @param in
     * @param fileName
     * @return
     * @throws Exception
     */
    public List getListByExcel(InputStream in, String fileName) throws Exception {
        List list = new ArrayList<>();
        //创建Excel工作薄
        Workbook work = this.getWorkbook(in, fileName);
        if (null == work) {
            throw new Exception("创建Excel工作薄为空！");
        }
        Sheet sheet = null;
        Row row = null;
        Cell cell = null;

        for (int i = 0; i < work.getNumberOfSheets(); i++) {
            sheet = work.getSheetAt(i);
            if (sheet == null) {
                continue;
            }

            for (int j = sheet.getFirstRowNum(); j <= sheet.getLastRowNum(); j++) {
                row = sheet.getRow(j);
                if (row == null || row.getFirstCellNum() == j) {
                    continue;
                }

                List<Object> li = new ArrayList<>();
                for (int y = row.getFirstCellNum(); y < row.getLastCellNum(); y++) {
                    cell = row.getCell(y);
                    li.add(cell);
                }
                list.add(li);
            }
        }
        work.close();
        return list;
    }

    /**
     * 判断文件格式
     *
     * @param inStr
     * @param fileName
     * @return
     * @throws Exception
     */
    public Workbook getWorkbook(InputStream inStr, String fileName) throws Exception {
        Workbook workbook = null;
        String fileType = fileName.substring(fileName.lastIndexOf("."));
        if (".xls".equals(fileType)) {
            workbook = new HSSFWorkbook(inStr);
        } else if (".xlsx".equals(fileType)) {
            workbook = new XSSFWorkbook(inStr);
        } else {
            throw new Exception("请上传excel文件！");
        }
        return workbook;
    }

    public ExcleColumn getExcleColumn(List<Object> columns ) throws  Exception{
        ExcleColumn excleColumn = new ExcleColumn();
        /**
         * 0 判断是否3级菜单
         * 1 上级菜单名称
         * 2 上级菜单编码
         * 3 功能名称
         * 4 功能命名
         * 5 字段
         * 6 查询条件字段
         * 7 select选择器的字段
         * 8 timePicked时间选择器字段
         * 9 datePicked日期选择器
         *10 datetimePicked日期时间选择器

         */
        String column0 = ((XSSFCell) columns.get(0)).toString();
        String column1 = ((XSSFCell) columns.get(1)).toString();
        String column2 = ((XSSFCell) columns.get(2)).toString();
        String column3 = ((XSSFCell) columns.get(3)).toString();
        String column4 = ((XSSFCell) columns.get(4)).toString();
        String column5 = ((XSSFCell) columns.get(5)).toString();
        String column6 = ((XSSFCell) columns.get(6)).toString();
        String column7 = ((XSSFCell) columns.get(7)).toString();
        String column8= ((XSSFCell) columns.get(8)).toString();
        String column9 = ((XSSFCell) columns.get(9)).toString();
        String column10 = ((XSSFCell) columns.get(10)).toString();



        List<Field> fieldList = new ArrayList<>();

        if ("是".equals(column0)) {
            excleColumn.setThreeListTreeFlag(true);
        }
        if (checkStringNull(column1)) {
            excleColumn.setSuperTreeName(column1);
        }
        if (checkStringNull(column2)) {
            excleColumn.setSuperTreeCode(column2);
        }
        if (checkStringNull(column3)) {
            excleColumn.setFileName(column3);
        }
        if (checkStringNull(column4)) {
            excleColumn.setFileCode(column4);
        }
        //设置字段
        if (checkStringNull(column5)) {
            String[] names = column5.split(",");
            for(int i=0; i < names.length; i++) {
                Field field = new Field();
                String name = names[i];
                field.setFieldName(name);
                field.setFieldCode(PinyinUtil.getPinyinFirst(name));
                fieldList.add(field);
            }
        }

        //0 查询条件字段
        if (checkStringNull(column6)) {
            setFlagByType(fieldList, column6, 0);
        }
        //设置高级查询
        int countQuery = 0;
        for (int i = 0; i < fieldList.size(); i++) {
            Field field = fieldList.get(i);
            boolean isQuery = field.isQueryFlag();
            if (isQuery) {
                countQuery++;
            }
        }
        if (countQuery > 4) {
            excleColumn.setExtendQuery(true);
        }
        //1 select 选择器
        if (checkStringNull(column7)) {
            setFlagByType(fieldList, column7, 1);

            //存储select下拉框的key value值
            String[] keyValueArray = column7.split(",");
            Map<String, String> keyValueMap = new HashMap<>();
            for (int i = 0; i < keyValueArray.length; i++) {
                String str = keyValueArray[i];
                String key = str.substring(0, str.indexOf("("));
                String value = str.substring(str.indexOf("(") + 1, str.indexOf(")"));
                keyValueMap.put(key, value);
            }
            excleColumn.setSelectKeyValueMap(keyValueMap);

        }
        //2 timePicked选择器
        if (checkStringNull(column8)) {
            setFlagByType(fieldList, column8, 2);
        }
        //3 DatePicker 日期选择器
        if (checkStringNull(column9)) {
            setFlagByType(fieldList, column9, 3);
        }
        //4 DateTimePicker 日期选择器
        if (checkStringNull(column10)) {
            setFlagByType(fieldList, column10, 4);
        }

        excleColumn.setFields(fieldList);
        return excleColumn;


    }

    /**
     *  输出所有文件数据：
     *  一、拼装字符串

     *  二、输出文件:文件命名+路径
     *
     * @param excleColumn
     */
    public void outPutDataFile(ExcleColumn excleColumn) {
        List<String> outPutStringList = new ArrayList<>();
        getData(excleColumn, outPutStringList);
        outputFile(excleColumn, outPutStringList);
        System.out.print(outPutStringList);
    }

    /**
     * 1 页面：list.vue add.vue  根据flag决定输入框的样式
     *  2 api 方法
     *  3 mock 模拟数据
     *  4 route 路由
     * @param excleColumn
     * @param outPutStringList 0:list.vue  1: add.vue  2:api 3：mock/index.js  4:mock/login.js  5:route/index.js
     * @return
     */
    public  List<String> getData(ExcleColumn excleColumn, List<String> outPutStringList){
        //拼接页面的内容
        getVueData(excleColumn, outPutStringList);
        //拼接api的内容
        getApiData(excleColumn, outPutStringList);
        //拼接mock中的内容
        getMockData(excleColumn, outPutStringList);
        //拼接route中的内容
        getRouteData(excleColumn, outPutStringList);
        return outPutStringList;
    }

    /**
     * 拼接vue页面
     * @param excleColumn
     * @param outPutStringList
     */
    public  void getVueData(ExcleColumn excleColumn, List<String> outPutStringList) {
        List<Field> fields = excleColumn.getFields();
        //拼接list.vue页面
        String listStr =   "<template>\n" +
                                    "  <div class=\"app-container\">\n" +
                                    "    <el-card class=\"box-card\" style=\"background-color:#fafafa;\">\n" +
                                    "       <div class=\"cardBack\">\n" +
                                    "          <!-- 搜索域 开始-->\n" +
                                    "          <div class=\"filter-container\">\n" +
                                    "            <el-form :model=\"listQuery\" ref=\"queryform\">\n" +
                                    "              <el-row :gutter=\"5\">\n";
        for (int i = 0; i < fields.size()&& i < 4; i++) {
            Field field = fields.get(i);
            String fieldName = field.getFieldName();
            int fieldLength = fieldName.length();
            if(field.isQueryFlag()){
                if (field.isSelectFlag()) {
                    Map<String, String> selectKeyValueMap = excleColumn.getSelectKeyValueMap();
                    listStr +=
                                    "                <el-col :xs=\"24\" :sm=\"12\" :md=\"4\" :lg=\"4\" :xl=\"4\">\n" +
                                    "                  <el-form-item label=\""+field.getFieldName()+"\" label-width=\""+  20*fieldLength+"px\" prop=\""+field.getFieldCode()+"\">\n" +
                                    "                    <el-select clearable  v-model=\"listQuery."+field.getFieldCode()+"\" placeholder=\""+field.getFieldName()+"\">\n";

                    String keyValueString = selectKeyValueMap.get(fieldName);
                    String[] keyValueArray = keyValueString.split(";");
                    for (int j = 0; j < keyValueArray.length; j++) {
                        String keyValue = keyValueArray[j];
                        String value = keyValue.split(":")[0];
                        String label = keyValue.split(":")[1];
                        listStr +=
                                    "                      <el-option label=\""+label+"\" value=\""+value+"\"></el-option>\n";
                    }
                    listStr +=
                                    "                    </el-select>\n" +
                                    "                  </el-form-item>\n" +
                                    "                </el-col>\n";

                }else
                //TODO  其他类型的查询条件
                {
                    listStr = listStr +
                            "                <el-col :xs=\"24\" :sm=\"12\" :md=\"4\" :lg=\"4\" :xl=\"4\">\n" +
                            "                  <el-form-item label= \"" + field.getFieldName() + "\" label-width=\""+ 20 * fieldLength +"px\" prop=\""+field.getFieldCode()+"\">\n" +
                            "                    <el-input v-model=\"listQuery." + field.getFieldCode() + "\" placeholder=\"" + field.getFieldName() + "\"></el-input>\n" +
                            "                  </el-form-item>\n" +
                            "                </el-col> \n" +
                            "\n";
                }
            }
        }
        if(excleColumn.isExtendQuery()){
            listStr +=
                    "           <el-col :xs=\"24\" :sm=\"12\" :md=\"6\" :lg=\"6\" :xl=\"6\">\n" +
                    "                  <el-form-item label-width=\"30px\">\n" +
                    "                      <el-tooltip class=\"item\" effect=\"dark\" content=\"查询\" placement=\"bottom\">\n" +
                    "                        <el-button size=\"mini\" class=\"filter-item\" type=\"primary\" circle icon=\"el-icon-search\" @click=\"getList\"></el-button>\n" +
                    "                      </el-tooltip>\n" +
                    "                      <el-tooltip class=\"item\" effect=\"dark\" content=\"重置\" placement=\"bottom\">\n" +
                    "                        <el-button size=\"mini\" class=\"filter-item\" type=\"primary\" circle icon=\"el-icon-refresh\" @click=\"getList\"></el-button>\n" +
                    "                      </el-tooltip>\n" +
                    "                      <el-tooltip class=\"item\" effect=\"dark\" content=\"高级查询\"  placement=\"bottom\">\n" +
                    "                        <el-button size=\"mini\" class=\"filter-item\" type=\"primary\" circle icon=\"el-icon-zoom-in\"  @click=\"showMoreCons\"></el-button>\n" +
                    "                      </el-tooltip>\n" +
                    "                      <el-tooltip class=\"item\" effect=\"dark\" content=\"新增\" placement=\"bottom\">\n" +
                    "                        <el-button size=\"mini\" class=\"filter-item\"  type=\"primary\" circle  icon=\"el-icon-plus\"  @click=\"handleAdd('add')\"></el-button>\n" +
                    "                      </el-tooltip>\n" +
                    "                      <el-tooltip class=\"item\" effect=\"dark\" content=\"启用\" placement=\"bottom\">\n" +
                    "                        <el-button size=\"mini\" class=\"filter-item\"  type=\"primary\" circle  icon=\"el-icon-check\"  @click=\"handleEnable\" ></el-button>\n" +
                    "                      </el-tooltip>\n" +
                    "                      <el-tooltip class=\"item\" effect=\"dark\" content=\"停用\" placement=\"bottom\">\n" +
                    "                        <el-button size=\"mini\" class=\"filter-item\"  type=\"primary\" circle icon=\"el-icon-close\" @click=\"handleDisable\" ></el-button>\n" +
                    "                      </el-tooltip>\n" +
                    "                  </el-form-item>\n" +
                    "                </el-col>\n" +
                    "              </el-row>\n";
        }else{
            listStr +=
                    "               <el-col :xs=\"24\" :sm=\"12\" :md=\"6\" :lg=\"6\" :xl=\"6\">\n" +
                    "                  <el-form-item label-width=\"30px\">\n" +
                    "                      <el-tooltip class=\"item\" effect=\"dark\" content=\"查询\" placement=\"bottom\">\n" +
                    "                        <el-button size=\"mini\" class=\"filter-item\" type=\"primary\" circle icon=\"el-icon-search\" @click=\"getList\"></el-button>\n" +
                    "                      </el-tooltip>\n" +
                    "                      <el-tooltip class=\"item\" effect=\"dark\" content=\"重置\" placement=\"bottom\">\n" +
                    "                        <el-button size=\"mini\" class=\"filter-item\" type=\"primary\" circle icon=\"el-icon-refresh\" @click=\"getList\"></el-button>\n" +
                    "                      </el-tooltip>\n" +
                    "                      <el-tooltip class=\"item\" effect=\"dark\" content=\"新增\" placement=\"bottom\">\n" +
                    "                        <el-button size=\"mini\" class=\"filter-item\"  type=\"primary\" circle  icon=\"el-icon-plus\"  @click=\"handleAdd('add')\"></el-button>\n" +
                    "                      </el-tooltip>\n" +
                    "                      <el-tooltip class=\"item\" effect=\"dark\" content=\"启用\" placement=\"bottom\">\n" +
                    "                        <el-button size=\"mini\" class=\"filter-item\"  type=\"primary\" circle  icon=\"el-icon-check\"  @click=\"handleEnable\" ></el-button>\n" +
                    "                      </el-tooltip>\n" +
                    "                      <el-tooltip class=\"item\" effect=\"dark\" content=\"停用\" placement=\"bottom\">\n" +
                    "                        <el-button size=\"mini\" class=\"filter-item\"  type=\"primary\" circle icon=\"el-icon-close\" @click=\"handleDisable\" ></el-button>\n" +
                    "                      </el-tooltip>\n" +
                    "                  </el-form-item>\n" +
                    "                </el-col>\n" +
                    "              </el-row>\n";
        }
        if (excleColumn.isExtendQuery()) {

            listStr += "              <!-- 隐藏的高级搜索域 -->\n" +
                    "              <el-row v-show=\"showMore\">\n";
            for (int i = 4; i < fields.size(); i++) {
                Field field = fields.get(i);
                String fieldName = field.getFieldName();
                int fieldLength = fieldName.length();
                if(field.isQueryFlag()){
                    if (field.isSelectFlag()) {
                        Map<String, String> selectKeyValueMap = excleColumn.getSelectKeyValueMap();
                        listStr +=
                                        "                <el-col :xs=\"24\" :sm=\"12\" :md=\"4\" :lg=\"4\" :xl=\"4\">\n" +
                                        "                  <el-form-item label=\""+field.getFieldName()+"\" label-width=\""+  20*fieldLength+"px\">\n" +
                                        "                    <el-select clearable  v-model=\"listQuery."+field.getFieldCode()+"\" placeholder=\""+field.getFieldName()+"\">\n";

                        String keyValueString = selectKeyValueMap.get(fieldName);
                        String[] keyValueArray = keyValueString.split(";");
                        for (int j = 0; j < keyValueArray.length; j++) {
                            String keyValue = keyValueArray[j];
                            String value = keyValue.split(":")[0];
                            String label = keyValue.split(":")[1];
                            listStr +=
                                       "                      <el-option label=\""+label+"\" value=\""+value+"\"></el-option>\n";
                        }
                        listStr +=
                                        "                    </el-select>\n" +
                                        "                  </el-form-item>\n" +
                                        "                </el-col>\n";

                    }else
                    //TODO  其他类型的查询条件
                    {
                        listStr +=
                                "                <el-col :xs=\"24\" :sm=\"12\" :md=\"4\" :lg=\"4\" :xl=\"4\">\n" +
                                "                  <el-form-item label= \"" + field.getFieldName() + "\" label-width=\""+ 20 * fieldLength +"px\">\n" +
                                "                    <el-input v-model=\"listQuery." + field.getFieldCode() + "\" placeholder=\"" + field.getFieldName() + "\"></el-input>\n" +
                                "                  </el-form-item>\n" +
                                "                 </el-col> \n";
                    }
                }
            }
            listStr += "              </el-row>\n";
        }
        listStr += "            </el-form>\n" +
                         "          </div>\n" +
                "          <!-- 搜索域 结束-->\n";


        listStr +=    "          <!-- 列表域 开始-->\n" +
                            "          <template>\n" +
                            "            <el-table :data=\"list\" size=\"small\" v-loading=\"listLoading\" tooltip-effect=\"dark\" stripe fit border highlight-current-row  height=365 @selection-change=\"changeSelections\" >\n" +
                            "              <el-table-column type=\"selection\" width=\"50\">\n" +
                            "              </el-table-column>\n" +
                            "              <el-table-column type=\"index\" width=\"50\" label=\"序号\" fixed=\"left\">\n" +
                            "              </el-table-column>\n";
        for(int i = 0; i<fields.size(); i++) {
            String fieldCode = fields.get(i).getFieldCode();
            String fieldName = fields.get(i).getFieldName();
            listStr += "              <el-table-column prop=\""+fieldCode+"\" show-overflow-tooltip label=\""+fieldName+"\"  >\n" +
                             "              </el-table-column>\n";
        }
        listStr +="              <el-table-column align=\"center\" show-overflow-tooltip label=\"操作\" width=\"150\" class-name=\"small-padding \" fixed=\"right\">\n" +
                        "                <template slot-scope=\"scope\">\n" +
                        "                  <el-button type=\"danger\" title=\"删除\" size=\"mini\" circle  icon=\"el-icon-delete\" @click=\"handleDel(scope.row.ckbh)\" />\n" +
                        "                  <el-button type=\"primary\" title=\"修改\" size=\"mini\" circle icon=\"el-icon-edit\" @click=\"handleAdd(scope.row)\" />\n" +
                        "                </template>\n" +
                        "              </el-table-column>\n" +
                        "            </el-table>\n" +
                        "          </template>\n";

        listStr +="          <div class=\"pagination-container\">\n" +
                        "            <el-pagination background @size-change=\"handleSizeChange\" @current-change=\"handleCurrentChange\" :current-page=\"listQuery.pageNum\" :page-sizes=\"[10,20,30,50]\" :page-size=\"listQuery.pageSize\" layout=\"total, sizes, prev, pager, next, jumper\" :total=\"total\">\n" +
                        "            </el-pagination>\n" +
                        "          </div>\n" +
                        "          <!-- 列表域 结束-->\n" +
                        "          <!-- 弹出框新增与修改 开始-->\n";
        listStr +="          <el-dialog title=\""+excleColumn.getFileName()+"\" :visible.sync=\"dialogCkformVisible\" :close-on-click-modal=\"false\" :show-close=\"false\" width=\"80%\">\n" +
                        "            <el-form :model=\""+excleColumn.getFileCode()+"form\" :rules=\"rules\" ref=\""+excleColumn.getFileCode()+"form\">\n";
        for (int i = 0; i < fields.size(); i++) {
            int elRow = i % 4;//余数为0时另起一行

            if (elRow == 0 && i!=fields.size()-1) {
                listStr += "              <el-row>\n";
            }
            Field field = fields.get(i);
            String fieldName = field.getFieldName();
            int fieldLength = fieldName.length();
            if(field.isQueryFlag()){
                if (field.isSelectFlag()) {
                    Map<String, String> selectKeyValueMap = excleColumn.getSelectKeyValueMap();
                    listStr +=
                            "              <el-col :xs=\"24\" :sm=\"12\" :md=\"6\" :lg=\"6\" :xl=\"6\">\n" +
                            "                 <el-form-item label=\""+field.getFieldName()+"\" label-width=\""+  20*fieldLength+"px\">\n" +
                            "                  <el-select clearable  v-model=\""+excleColumn.getFileCode()+"form."+field.getFieldCode()+"\" placeholder=\""+field.getFieldName()+"\">\n";
                    String keyValueString = selectKeyValueMap.get(fieldName);
                    String[] keyValueArray = keyValueString.split(";");
                    for (int j = 0; j < keyValueArray.length; j++) {
                        String keyValue = keyValueArray[j];
                        String value = keyValue.split(":")[0];
                        String label = keyValue.split(":")[1];
                        listStr +=
                                "                     <el-option label=\""+label+"\" value=\""+value+"\"></el-option>\n";
                    }
                    listStr +=
                                    "                        </el-select>\n" +
                                    "                      </el-form-item>\n" +
                                    "                    </el-col>\n";

                }else
                //TODO  其他类型的查询条件
                {
                    listStr +=
                            "               <el-col :xs=\"24\" :sm=\"12\" :md=\"6\" :lg=\"6\" :xl=\"6\">\n" +
                            "                   <el-form-item label= \"" + field.getFieldName() + "\" label-width=\""+ 20 * fieldLength +"px\">\n" +
                            "                       <el-input v-model=\""+excleColumn.getFileCode()+"form." + field.getFieldCode() + "\" placeholder=\"" + field.getFieldName() + "\"></el-input>\n" +
                            "                   </el-form-item>\n" +
                            "               </el-col> \n" +
                            "\n";
                }
            }
            if (elRow == 3) {
                listStr += "              </el-row>\n";
            }
            if (i == fields.size() - 1 && elRow != 0) {
                listStr += "              </el-row>\n";
            }
        }
        listStr += "            </el-form>\n" +
                "            <div slot=\"footer\" class=\"dialog-footer\">\n" +
                "              <el-button @click=\"dialogCkformVisible = false\">取 消</el-button>\n" +
                "              <el-button type=\"primary\" @click=\"dialogCkformVisible = false\">确 定</el-button>\n" +
                "            </div>\n" +
                "          </el-dialog>\n" +
                "          <!-- 弹出框新增与修改 结束-->\n" +
                "      </div>\n" +
                "    </el-card>\n" +
                "  </div>\n" +
                "</template>\n";

        listStr +=    "<script>\n" +
                            "import { fetchPageList,executeDel,executeAdd,executeUpdate,executeEnable,executeDisable} from \"@/api/wms/"+ excleColumn.getSuperTreeCode() +"/"+excleColumn.getFileCode()+"api"+"\";\n" +
                            "import waves from \"@/directive/waves\";\n" +
                            "\n" +
                            "export default {\n" +
                            "  directives: {\n" +
                            "    waves\n" +
                            "  },\n" +
                            "  data() { \n" +
                            "    return {\n" +
                            "      selectListRow: [], ///行集合\n" +
                            "      showMore: false,  ///高级查询隐藏显示控制标识\n" +
                            "      isAdd:false,        ///判断是否是新增状态\n" +
                            "      list: null,    ////列表集合\n" +
                            "      total: null,   ////集合总数\n" +
                            "      listLoading: true,   ////加载标识\n" +
                            "      listQuery: {    ////搜索条件\n" +
                            "        pageNum: 1,\n" +
                            "        pageSize: 10, \n";
        for (int i = 0; i < fields.size(); i++) {
            Field field = fields.get(i);
            String fieldCode = field.getFieldCode();
            if(field.isQueryFlag()){
                listStr += "        "+ fieldCode + ":\"\",\n";
            }
        }
        listStr +="  },\n" +
                "      dialogCkformVisible: false,   \n" +
                "      "+excleColumn.getFileCode()+"form: {\n";
        for (int i = 0; i < fields.size(); i++) {
            Field field = fields.get(i);
            String fieldCode = field.getFieldCode();
            if(field.isQueryFlag()){
                listStr += "        "+ fieldCode + ":\"\",\n";
            }
        }
        listStr += "      }," +
                "rules: {\n" +
                "        \n" +
                "      }\n" +
                "    };\n" +
                "  },\n" +
                "  created() {    /////初始化方法\n" +
                "    this.getList();\n" +
                "  },\n" +
                "  methods: {\n";
        if (excleColumn.isExtendQuery()) {
            listStr += " //高级查询控制方法\n" +
                    "    showMoreCons() {\n" +
                    "      this.showMore == false ? (this.showMore = true) : (this.showMore = false);\n" +
                    "    },\n";
        }
        listStr +=
                "     handleAdd(val){\n" +
                "      if (val == \"add\") {\n";
        for (int i = 0; i < fields.size(); i++) {
            Field field = fields.get(i);
            String fieldCode = field.getFieldCode();
            listStr += "        this."+excleColumn.getFileCode()+"form."+fieldCode+"=\"\"; \n";
        }
        listStr += "        this.isAdd=true;\n" +
                          "        this.edit=false\n" +
                          "      } else {\n";
        for (int i = 0; i < fields.size(); i++) {
            Field field = fields.get(i);
            String fieldCode = field.getFieldCode();
            listStr += "        this."+excleColumn.getFileCode()+"form."+fieldCode+"=val."+fieldCode+"; \n";
        }
        listStr += "        this.isAdd=false;\n" +
                          "         this.edit=true\n" +
                        "      }\n" +
                        "      this.dialogCkformVisible = true;\n" +
                        "    },\n" +
                        "    //重置\n" +
                        "    reset(){\n" +
                        "      if(this.$refs.queryform != undefined){\n" +
                        "        this.$refs.queryform.resetFields();\n" +
                        "      }\n" +
                        "    }," +
                        "   handleDel(ckbh) {";
        listStr += "this.$confirm(\"确认删除该"+excleColumn.getFileName()+"?\",\"提示\",";
        listStr += "{\n" +
                "                      closeOnClickModal:false,\n" +
                "                      showClose:false,\n" +
                "                      type: 'warning'\n" +
                "                    })\n" +
                "        .then(_ => {\n" +
                "          this."+excleColumn.getFileCode()+"form.ckbh = ckbh;\n" +
                "          executeDel(this."+excleColumn.getFileCode()+"form).then(response => {\n" +
                "            if (response.state) {\n" +
                "              this.$message({\n" +
                "                message: response.msg,\n" +
                "                type: \"success\"\n" +
                "              });\n" +
                "              this.getList();\n" +
                "            } else {\n" +
                "              this.$message({\n" +
                "                message: response.msg,\n" +
                "                type: \"error\"\n" +
                "              });\n" +
                "            }\n" +
                "          });\n" +
                "        })\n" +
                "        .catch(err => {\n" +
                "          console.log(err);\n" +
                "        });\n" +
                "    },\n" +

                "    handleEnable(){\n" +
                "      if(this.selectListRow.length > 0){\n" +
                "        this.$confirm(\"确认启用选中数据?\",{\n" +
                "              closeOnClickModal:false,\n" +
                "              showClose:false,\n" +
                "              type: 'warning'\n" +
                "        }).then(_ => {\n" +
                "          executeEnable(this.selectListRow).then(response => {\n" +
                "            if (response) {\n" +
                "              this.$message({\n" +
                "                message: response.msg,\n" +
                "                type: \"success\"\n" +
                "              });\n" +
                "              this.getList();\n" +
                "            } else {\n" +
                "              this.$message({\n" +
                "                message: response.msg,\n" +
                "                type: \"error\"\n" +
                "              });\n" +
                "            }\n" +
                "          });\n" +
                "        });\n" +
                "      }else{\n" +
                "        this.$message({\n" +
                "          message: \"请至少选择一条数据\",\n" +
                "          type: \"error\"\n" +
                "        });\n" +
                "      }\n" +
                "    },\n" +

                "    handleDisable(){\n" +
                "      if(this.selectListRow.length > 0){\n" +
                "        this.$confirm(\"确认停用选中数据?\",{\n" +
                "              closeOnClickModal:false,\n" +
                "              showClose:false,\n" +
                "              type: 'warning'\n" +
                "      }).then(_ => {\n" +
                "          executeDisable(this.selectListRow).then(response => {\n" +
                "            if (response) {\n" +
                "              this.$message({\n" +
                "                message: response.msg,\n" +
                "                type: \"success\"\n" +
                "              });\n" +
                "              this.getList();\n" +
                "            } else {\n" +
                "              this.$message({\n" +
                "                message: response.msg,\n" +
                "                type: \"error\"\n" +
                "              });\n" +
                "            }\n" +
                "          });\n" +
                "        });\n" +
                "      }else{\n" +
                "        this.$message({\n" +
                "          message: \"请至少选择一条数据\",\n" +
                "          type: \"error\"\n" +
                "        });\n" +
                "      }\n" +
                "      \n" +
                "    },\n" +
                "    handleSubmitForm(){\n" +
                "      this.$refs."+excleColumn.getFileCode()+"form.validate(valid => {\n" +
                "        if (valid) {\n" +
                "          alert(this.isAdd);\n" +
                "          if (this.isAdd) {\n" +
                "            executeAdd(this."+excleColumn.getFileCode()+"form).then(response => {\n" +
                "              alert(response);\n" +
                "               this.showResponse(response);\n" +
                "            });\n" +
                "          }else{\n" +
                "            executeUpdate(this."+excleColumn.getFileCode()+"form).then(response => {\n" +
                "               this.showResponse(response);\n" +
                "            });\n" +
                "          }\n" +
                "        } else {\n" +
                "          console.log(\"error submit!!\");\n" +
                "          return false;\n" +
                "        }\n" +
                "      }); \n" +
                "    },\n" +
                "    //抽取浏览器返回结果\n" +
                "    showResponse(response){\n" +
                "        if (response.state) {\n" +
                "          this.$notify({\n" +
                "            title: \"成功\",\n" +
                "            message: response.msg,\n" +
                "            type: \"success\",\n" +
                "            duration: 2000\n" +
                "          });\n" +
                "          this.dialogCkformVisible = false;\n" +
                "        } else {\n" +
                "          this.$notify({\n" +
                "            title: \"失败\",\n" +
                "            message: response.msg,\n" +
                "            type: \"error\",\n" +
                "            duration: 2000\n" +
                "          });\n" +
                "        }\n" +
                "    },\n" +
                "    \n" +
                "    getList() {  //列表查询\n" +
                "      this.listLoading = true;\n" +
                "      fetchPageList(this.listQuery).then(response => {\n" +
                "        this.list = response.list;\n" +
                "        this.total = response.total;\n" +
                "        this.listLoading = false;\n" +
                "      });\n" +
                "    },\n" +
                "    handleSizeChange(val) {  //分页控件触发\n" +
                "      this.listQuery.pageSize = val;\n" +
                "      this.getList();\n" +
                "    },\n" +
                "    handleCurrentChange(val) {  //分页控件触发\n" +
                "      this.listQuery.pageNum = val;\n" +
                "      this.getList();\n" +
                "    },\n" +
                "    changeSelections(val) {  //获取选择的所有行集合\n" +
                "      //为selectListRow赋值为当前选中行的值\n" +
                "      this.selectListRow = val;\n" +
                "    },\n" +
                "  }\n" +
                "};\n" +
                "</script>\n";
        outPutStringList.add(0,listStr);

    }


    /**
     * 拼接api
     * @param excleColumn
     * @param outPutStringList
     */
    public  void getApiData(ExcleColumn excleColumn, List<String> outPutStringList) {
        String superCode = excleColumn.getSuperTreeCode();
        String code = excleColumn.getFileCode();
        String upCode = code.substring(0, 1).toUpperCase() + code.substring(1);
        String apiStr = "import request from '@/utils/request'\n" +
                "\n" +
                "export function fetchPageList(query) {\n" +
                "  return request({\n" +
                "    api: 'BAPI'," +
                "    url: '/w-ckx/" + code + "/pageList',\n" +
                "    method: 'get',\n" +
                "    params: query\n" +
                "  })\n" +
                "}\n" +
                "\n" +
                "export function executeDel(data) {\n" +
                "  return request({\n" +
                "    api: 'BAPI'," +
                "    url: '/w-ckx/" + code + "/del',\n" +
                "    method: 'delete',\n" +
                "    data \n" +
                "  })\n" +
                "}\n" +
                "\n" +
                "export function executeAdd(data) {\n" +
                "  return request({\n" +
                "    api: 'BAPI'," +
                "    url: '/w-ckx/" + code + "/add',\n" +
                "    method: 'post',\n" +
                "    data \n" +
                "  })\n" +
                "}\n" +
                "\n" +
                "export function executeUpdate(data) {\n" +
                "  return request({\n" +
                "    api: 'BAPI'," +
                "    url: '/w-ckx/" + code + "/update',\n" +
                "    method: 'post',\n" +
                "    data\n" +
                "  })\n" +
                "}\n" +
                "\n" +
                "export function executeEnable(data) {\n" +
                "  return request({\n" +
                "    api: 'BAPI'," +
                "    url: '/w-ckx/" + code + "/enable',\n" +
                "    method: 'post',\n" +
                "    data\n" +
                "  })\n" +
                "}\n" +
                "export function executeDisable(data) {\n" +
                "  return request({\n" +
                "    api: 'BAPI'," +
                "    url: '/w-ckx/" + code + "/disable',\n" +
                "    method: 'post',\n" +
                "    data \n" +
                "  })\n" +
                "}";
        outPutStringList.add(1, apiStr);
    }

    /**
     * 拼接mock
     * @param excleColumn
     * @param outPutStringList
     */
    public  void getMockData(ExcleColumn excleColumn, List<String> outPutStringList) {
        List<Field> fieldList = excleColumn.getFields();
        String superCode = excleColumn.getSuperTreeCode();
        String code = excleColumn.getFileCode();
        String name = excleColumn.getFileName();
        String mockIndex = "import "+code+"API from './wms/ckx/"+code+"/"+code+"mock'\n";
        mockIndex +=
                "//" + name + "\n" +
                        "Mock.mock(/\\/w-ckx\\/" + code + "\\/pageList/, 'get', "+code+"API.pageList)\n" +
                        "Mock.mock(/\\/w-ckx\\/" + code + "\\/add/, 'post', "+code+"API.addResult)\n" +
                        "Mock.mock(/\\/w-ckx\\/" + code + "\\/del/, 'post', "+code+"API.delResult)\n" +
                        "Mock.mock(/\\/w-ckx\\/" + code + "\\/update/, 'post', "+code+"API.updateResult)\n" +
                        "Mock.mock(/\\/w-ckx\\/" + code + "\\/enable/, 'post', "+code+"API.enableResult)\n" +
                        "Mock.mock(/\\/w-ckx\\/" + code + "\\/disable/, 'post', "+code+"API.disableResult)\n";

        String mockLogin = "const Mock = require(\"mockjs\")\n" +
                "\n" +
                "export default {\n" +
                "    //"+excleColumn.getFileName()+"\n" +
                "    pageList: config => {\n" +
                "        return {\"pageNum\":1,\"pageSize\":10,\"size\":2,\"startRow\":1,\"endRow\":2,\"total\":2,\"pages\":1,\n" +
                "        \"list\":[\n";
        for (int i = 0; i < 20; i++) {
            mockLogin += "            {\"pageNum\":1,\"pageSize\":10,\"userId\":\"" + i + "\"";
            for (int j = 0; j < fieldList.size(); j++) {
                Field field =  fieldList.get(j);
                String fieldCode = field.getFieldCode();
                mockLogin += ",\""+fieldCode+"\":\"column"+(j+1)+"\"";
            }
            mockLogin += "},\n";
        }

        mockLogin +=   "        ],\n" +
                                    "        \"prePage\":0,\"nextPage\":0,\"isFirstPage\":true,\"isLastPage\":true,\"hasPreviousPage\":false,\"hasNextPage\":false,\"navigatePages\":8,\"navigatepageNums\":[1],\"navigateFirstPage\":1,\"navigateLastPage\":1,\"lastPage\":1,\"firstPage\":1}  \n" +
                                    "    },\n" +
                                    "    enableResult: config => {\n" +
                                    "        return {\"sfhs\":true,\"msg\":\"启用成功\",\"data\":null,\"code\":200}  \n" +
                                    "    },\n" +
                                    "    disableResult: config => {\n" +
                                    "        return {\"sfhs\":true,\"msg\":\"停用成功\",\"data\":null,\"code\":200}  \n" +
                                    "    },\n" +
                                    "    addResult: config => {\n" +
                                    "        return {\"sfhs\":true,\"msg\":\"新增成功\",\"data\":null,\"code\":200}  \n" +
                                    "    },\n" +
                                    "    delResult: config => {\n" +
                                    "        return {\"sfhs\":true,\"msg\":\"删除成功\",\"data\":null,\"code\":200}  \n" +
                                    "    },\n" +
                                    "    updateResult: config => {\n" +
                                    "        return {\"sfhs\":true,\"msg\":\"修改成功\",\"data\":null,\"code\":200}  \n" +
                                    "    }\n" +
                                    "}";
        outPutStringList.add(2,mockIndex);
        outPutStringList.add(3,mockLogin);
    }

    /**
     * 拼接route
     * @param excleColumn
     * @param outPutStringList
     */
    public  void getRouteData(ExcleColumn excleColumn, List<String> outPutStringList) {
        String superCode = excleColumn.getSuperTreeCode();
        String code = excleColumn.getFileCode();
        String name = excleColumn.getFileName();
        String routeString ="       { path: '" + code + "', component: () => import('@/views/wms/" + superCode + "/" + code +"/list'), name: '" +code+ "', meta: { title: '" + name + "', noCache: true, menucode: '"+ code+ "Manager' }},\n";
        outPutStringList.add(4,routeString);

    }

    /**
     * 创建文件，将list中的数据输出到指定文件中
     * 6个文件
     * 目录结构：
     * -src
     * --api
     * ----?
     * ------?.js
     * --mock
     * ----index.js
     * ----login.js
     * --route
     * ----index.js
     * --view
     * ----?
     * ------?
     * --------list.vue
     * --------add.vue
     * @param outPutStringList 0:list.vue  1: add.vue  2:api 3：mock/index.js  4:mock/login.js  5:route/index.js
     */
    public void  outputFile(ExcleColumn excleColumn, List<String> outPutStringList ){
        String superTreeCode = excleColumn.getSuperTreeCode();
        String code = excleColumn.getFileCode();
        String apiDir = "G://src/api/wms/" + superTreeCode;
        String mockPathDir = "G://src/mock";
        String mockDataDir = "G://src/mock/wms/" +  superTreeCode+ "/" +code;
        String routeDir = "G://src/route";
        String viewDir = "G://src/view/" + superTreeCode +"/" + code;
        File directory = new File(apiDir);
        if (!directory.exists()) {
            directory.mkdirs();
        }
        directory = new File(mockPathDir);
        if (!directory.exists()) {
            directory.mkdirs();
        }
        directory = new File(mockDataDir);
        if (!directory.exists()) {
            directory.mkdirs();
        }
        directory = new File(routeDir);
        if (!directory.exists()) {
            directory.mkdirs();
        }
        directory = new File(viewDir);
        if (!directory.exists()) {
            directory.mkdirs();
        }
        File vueList = new File(viewDir+ "/list.vue");
        File vueAdd = new File(viewDir+ "/add.vue");
        File apiFile = new File(apiDir + "/" + code + "api.js");
        File mockPath = new File(mockPathDir + "/index.js");
        File mockData = new File(mockDataDir + "/"+ code+"mock.js");
        File routeFile = new File(routeDir+ "/index.js");

        createFile(vueList, outPutStringList.get(0));
        createFile(apiFile, outPutStringList.get(1));
        appendFile(mockPath, outPutStringList.get(2));
        createFile(mockData, outPutStringList.get(3));
        appendFile(routeFile, outPutStringList.get(4));

    }

    /**
     * 创建文件
     * @param file
     * @param str
     */
    public void createFile(File file,String str) {
        try {
            if (!file.exists()) {
                file.createNewFile();
            }
            PrintWriter myFile = new PrintWriter(new OutputStreamWriter(new FileOutputStream(file),"utf-8"));
            myFile.println(str);
            myFile.close();
        } catch (Exception e) {
            System.out.println("新建文件操作出错");
            e.printStackTrace();
        }
    }

    /**
     * 追加文件
     * @param file
     * @param str
     */
    public void appendFile(File file,String str) {
        BufferedWriter out = null;
        try {
            out = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file, true), "utf-8"));
            out.write(str);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                out.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }



    public boolean checkStringNull(String string) {

        if ("".equals(string) || string == null) {
            return false;
        }
        return true;
    }

    /**
     * 设置字段的flag
     * @param fieldList
     * @param column
     * @param type   0:查询条件字段  1：select 选择器   2：timePicked时间选择器   3：DatePicker 日期选择器  4：DateTimePicker 日期时间选择器
     */
    public void setFlagByType(List<Field> fieldList, String column, int type) {
        String[] names = column.split(",");
        for(int i=0; i < names.length; i++) {
            String selectName = names[i];
            for (Field field : fieldList) {
                String fieldName =  field.getFieldName();
                if (fieldName.equals(selectName.split("\\(")[0])) {
                    if (type == 0) {
                        field.setQueryFlag(true);
                    }
                    if (type == 1) {
                        field.setSelectFlag(true);
                    }
                    if (type == 2) {
                        field.setTimePickerFlag(true);
                    }
                    if (type == 3) {
                        field.setDatePickerFlag(true);
                    }
                    if (type == 4) {
                        field.setDatePickerFlag(true);
                    }
                }
            }
        }
    }
}
