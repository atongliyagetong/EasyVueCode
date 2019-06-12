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
 * Created by Chen on 2019/3/3.
 */
@Service
public class ImportService {


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
        String listStr = "<template>\n" +
                "  <div class=\"app-container\">\n" +
                "    <div class=\"filter-container\">\n" +
                "\n" +
                "      <el-form >\n" +
                "        <el-row :gutter=\"20\">\n";
        for (int i = 0; i < fields.size(); i++) {
            Field field = fields.get(i);
            String fieldName = field.getFieldName();
            int fieldLength = fieldName.length();
            if(field.isQueryFlag()){
                if (field.isSelectFlag()) {
                    Map<String, String> selectKeyValueMap = excleColumn.getSelectKeyValueMap();
                    listStr +=
                            "          <el-col :xs=\"24\" :sm=\"12\" :md=\"6\" :lg=\"6\" :xl=\"6\">\n" +
                            "\n" +
                            "            <el-form-item label=\""+field.getFieldName()+"\" label-width=\""+  20*fieldLength+"px\">\n" +
                            "              <el-select v-model=\"listQuery."+field.getFieldCode()+"\" placeholder=\""+field.getFieldName()+"\">\n";
                    listStr +=
                            "                <el-option label=\"\" value></el-option>\n";

                    String keyValueString = selectKeyValueMap.get(fieldName);
                    String[] keyValueArray = keyValueString.split(";");
                    for (int j = 0; j < keyValueArray.length; j++) {
                        String keyValue = keyValueArray[j];
                        String value = keyValue.split(":")[0];
                        String label = keyValue.split(":")[1];
                        listStr +=
                                "                <el-option label=\""+label+"\" value=\""+value+"\"></el-option>\n";
                    }
                    listStr +=
                            "              </el-select>\n" +
                            "            </el-form-item>\n" +
                            "\n" +
                            "          </el-col>\n";

                }else
                //TODO  其他类型的查询条件
                {
                    listStr = listStr + "          <el-col :xs=\"24\" :sm=\"12\" :md=\"6\" :lg=\"6\" :xl=\"6\">\n" +
                            "\n" +
                            "            <el-form-item label= \"" + field.getFieldName() + "\" label-width=\""+ 20 * fieldLength +"px\">\n" +
                            "              <el-input v-model=\"listQuery." + field.getFieldCode() + "\" placeholder=\"" + field.getFieldName() + "\"></el-input>\n" +
                            "            </el-form-item>\n" +
                            "\n" +
                            "          </el-col> \n";
                }
            }
        }
        listStr += "        </el-row>\n";
        listStr += "        <el-row :gutter=\"2\" justify=\"end\" type=\"flex\">\n" +
                "          <el-col :xs=\"24\" :sm=\"12\" :md=\"8\" :lg=\"6\" :xl=\"6\">\n" +
                "            <div class=\"pull-right\">\n" +
                "              <el-button class=\"filter-item\" type=\"primary\" v-waves icon=\"el-icon-search\" @click=\"getList\">{{$t('table.search')}}</el-button>\n" +
                "\n" +
                "              <router-link :to=\"'/" + excleColumn.getSuperTreeCode() + "/" + excleColumn.getFileCode() + "/add'\">\n" +
                "                <el-button class=\"filter-item\" type=\"primary\" v-waves icon=\"el-icon-plus\"> 添加</el-button>\n" +
                "                <!-- <auth-button class=\"filter-item\" type=\"primary\" code=\"123\" v-waves icon=\"el-icon-plus\"> 添加</auth-button> -->\n" +
                "\n" +
                "              </router-link>\n" +
                "            </div>\n" +
                "          </el-col>\n" +
                "        </el-row>\n" +
                "      </el-form>\n" +
                "    </div>\n" +
                "\n" +
                "    <template>\n" +
                "      <el-table :data=\"list\" v-loading=\"listLoading\" tooltip-effect=\"dark\" border fit highlight-current-row style=\"width: 100%\" >\n";
        listStr += "        <el-table-column type=\"selection\" width=\"55\">\n" +
                         "        </el-table-column>\n" +
                         "        <el-table-column type=\"index\" width=\"100\" label=\"序号\" fixed=\"left\">\n" +
                         "        </el-table-column>";
        for(int i = 0; i<fields.size(); i++) {
            String fieldCode = fields.get(i).getFieldCode();
            String fieldName = fields.get(i).getFieldName();
            listStr += "        <el-table-column prop=\""+fieldCode+"\" show-overflow-tooltip label=\""+fieldName+"\"  >\n" +
                    "        </el-table-column>\n";
        }
        listStr +="        <el-table-column align=\"center\" show-overflow-tooltip label=\"操作\" width=\"160\" class-name=\"small-padding fixed-width\" fixed=\"right\">\n" +
                "          <template slot-scope=\"scope\">\n" +
                "            <el-button type=\"danger\" title=\"删除\" size=\"mini\" icon=\"el-icon-delete\" @click=\"handleDel(scope.row.userId)\"></el-button>\n" +
                "            <router-link :to=\"{path: '/"+excleColumn.getSuperTreeCode()+"/"+excleColumn.getFileCode()+"/edit', query: {id: scope.row.userId}}\">\n" +
                "              <el-button type=\"primary\" size=\"mini\" icon=\"el-icon-edit\"></el-button>\n" +
                "            </router-link>\n" +
                "          </template>\n" +
                "        </el-table-column>\n" +
                "      </el-table>\n" +
                "    </template>\n" +
                "\n" +
                "    <div class=\"pagination-container\">\n" +
                "      <el-pagination background @size-change=\"handleSizeChange\" @current-change=\"handleCurrentChange\" :current-page=\"listQuery.pageNum\" :page-sizes=\"[10,20,30, 60]\" :page-size=\"listQuery.pageSize\" layout=\"total, sizes, prev, pager, next, jumper\" :total=\"total\">\n" +
                "      </el-pagination>\n" +
                "    </div>\n" +
                "\n" +
                "  </div>\n" +
                "\n" +
                "</template>";
        String upFileCode = excleColumn.getFileCode().substring(0, 1).toUpperCase() + excleColumn.getFileCode().substring(1);
        listStr +="<script>\n" +
                "import { fetchList, delete"+ upFileCode +"  } from \"@/api/"+ excleColumn.getSuperTreeCode() +"/"+excleColumn.getFileCode()+"\";\n" +
                "import waves from \"@/directive/waves\";\n" +
                "\n" +
                "export default {\n" +
                "  directives: {\n" +
                "    waves\n" +
                "  },\n" +
                "  data() {\n" +
                "    return {\n" +
                "      list: null,\n" +
                "      total: null,\n" +
                "      listLoading: true,\n" +
                "      listQuery: {\n" +
                "        pageNum: 1,\n" +
                "        pageSize: 10,\n" +
                "        //dprtName: undefined\n" +
                "      }\n" +
                "    };\n" +
                "  },\n" +
                "  created() {\n" +
                "    this.getList();\n" +
                "  },\n" +
                "  methods: {\n" +
                "    getList() {\n" +
                "      this.listLoading = true;\n" +
                "      fetchList(this.listQuery).then(response => {\n" +
                "        this.list = response.list;\n" +
                "        this.total = response.total;\n" +
                "        this.listLoading = false;\n" +
                "      });\n" +
                "    },\n" +
                "    handleSizeChange(val) {\n" +
                "      this.listQuery.pageSize = val;\n" +
                "      this.getList();\n" +
                "    },\n" +
                "    handleCurrentChange(val) {\n" +
                "      this.listQuery.pageNum = val;\n" +
                "      this.getList();\n" +
                "    },\n" +
                "    handleDel(userId) {\n" +
                "      this.$confirm(\"确认删除该条数据？\")\n" +
                "        .then(_ => {\n" +
                "\n" +
                "              this.$message({\n" +
                "                message: \"删除成功\",\n" +
                "                type: \"success\"\n" +
                "              });\n" +
                "            \n" +
                "\n" +
                "        })\n" +
                "        .catch(err => {\n" +
                "          console.log(err);\n" +
                "        });\n" +
                "    }\n" +
                "  }\n" +
                "};\n" +
                "</script>";
        outPutStringList.add(0,listStr);
        //拼接add.vue
        String addStr = "<template>\n" +
                "  <div class=\"app-container\">\n" +
                "\n" +
                "    <el-row :gutter=\"20\">\n" +
                "      <el-col :xs=\"24\" :sm=\"24\" :md=\"12\" :lg=\"12\" :xl=\"12\">\n" +
                "        <el-card class=\"box-card\">\n" +
                "          <div slot=\"header\" class=\"clearfix\">\n" +
                "            <span>{{$route.meta.title}}</span>\n" +
                "          </div>\n" +
                "\n" +
                "         <el-form :model=\"from\" label-position=\"top\" :rules=\"rules\" ref=\"from\">\n" +
                "            <el-row :gutter=\"20\">\n";
        for (int i = 0; i < fields.size(); i++) {
            Field field = fields.get(i);
            String fieldCode = fields.get(i).getFieldCode();
            String fieldName= fields.get(i).getFieldName();
            Map<String, String> selectKeyValueMap = excleColumn.getSelectKeyValueMap();
            if (!field.isSelectFlag() && !field.isDatePickerFlag() && !field.isTimePickerFlag() && !field.isDateTimePickerFlag()) {
                addStr += "              <el-col :xs=\"24\" :sm=\"24\" :md=\"12\" :lg=\"12\" :xl=\"12\">\n" +
                        "                <el-form-item label=\"" + fieldName + "\" prop=\"" + fieldCode + "\">\n" +
                        "                   <el-input  v-model=\"from." + fieldCode + "\" ></el-input>\n" +
                        "                </el-form-item>\n" +
                        "              </el-col>\n";
            }
            if (field.isSelectFlag()) {


                addStr +=
                        "              <el-col :xs=\"24\" :sm=\"24\" :md=\"12\" :lg=\"12\" :xl=\"12\">\n" +
                        "                <el-form-item label=\""+fieldName+"\" prop=\""+fieldCode+"\" label-width=\"70px\">\n" +
                        "                  <el-select v-model=\"from."+fieldCode+"\" placeholder=\""+fieldName+"\" :xs=\"24\" :sm=\"24\" :md=\"12\" :lg=\"12\" :xl=\"12\">\n";
                String keyValueString = selectKeyValueMap.get(fieldName);
                String[] keyValueArray = keyValueString.split(";");
                for (int j = 0; j < keyValueArray.length ; j++) {
                    String keyValue = keyValueArray[j];
                    String value = keyValue.split(":")[0];
                    String label = keyValue.split(":")[1];
                    addStr +=
                            "                   <el-option label=\""+label+"\" value=\""+value+"\"></el-option>\n";
                }
                addStr +=
                        "                  </el-select>\n" +
                        "                </el-form-item>\n" +
                        "              </el-col>\n";
            }
            //TODO 其他类型的下拉框

        }
        addStr +=
                "            </el-row>\n" +
                "\n" +
                "            \n" +
                "            <el-row :gutter=\"20\">\n" +
                "              <el-col :xs=\"24\" :sm=\"24\" :md=\"24\" :lg=\"24\" :xl=\"24\">\n" +
                "                <el-form-item>\n" +
                "                  <el-button class=\"right-btn\" @click=\"back\">返回</el-button>\n" +
                "                  <el-button class=\"right-btn\" type=\"primary\" @click=\"submitForm()\">{{buttonname}}</el-button>\n" +
                "\n" +
                "                </el-form-item>\n" +
                "              </el-col>\n" +
                "            </el-row>\n" +
                "\n" +
                "          </el-form>\n" +
                "\n" +
                "        </el-card>\n" +
                "      </el-col>\n" +
                "\n" +
                "      \n" +
                "\n" +
                "    </el-row>\n" +
                "  </div>\n" +
                "</template>\n";
        //script
        addStr += "<script>\n" +
                "\n" +
                "import { edit" + upFileCode + ",add" + upFileCode + ",fetchRoleList,fetch" + upFileCode + "} from \"@/api/" + excleColumn.getSuperTreeCode() + "/" + excleColumn.getFileCode() + "\";\n" +
                "\n" +
                "export default {\n" +
                "  data() {\n" +
                "    return {\n" +
                "       //可修改\n" +
                "      allReadOnly: false,\n" +
                "      //不可修改\n" +
                "      readOnly: false,\n" +
                "      filterText: \"\",\n" +
                "      roleList:[],\n" +
                "      options:[],\n" +
                "      defaultProps: {\n" +
                "        children: \"children\",\n" +
                "        label: \"label\"\n" +
                "      },\n" +
                "      from: {\n" +
                "        userName: \"\",\n" +
                "        realName: \"\",\n" +
                "        cellphone: \"\",\n" +
                "        phone: \"\",\n" +
                "        sex: \"\",\n" +
                "        state: \"1\",\n" +
                "        dprtId: \"\",\n" +
                "        roleId:[]\n" +
                "      },\n" +
                "\n" +
                "      rules: {\n" +
                "        dprtCode: [\n" +
                "          { required: true, message: \"请输入菜单编号\", trigger: \"change\" }\n" +
                "        ],\n" +
                "        dprtName: [\n" +
                "          { required: true, message: \"请输入菜单名称\", trigger: \"change\" }\n" +
                "        ]\n" +
                "      }\n" +
                "    };\n" +
                "  },\n" +
                "\n" +
                "  watch: {\n" +
                "    filterText(val) {\n" +
                "      this.$refs.tree2.filter(val);\n" +
                "    }\n" +
                "  },\n" +
                "  created() {\n" +
                "    //this.getDeptList();\n" +
                "    this.getRoleList();\n" +
                "\n" +
                "    if (this.$route.meta.type === \"add\") {\n" +
                "      this.buttonname = \"新增\";\n" +
                "    } else {\n" +
                "      this.buttonname = \"修改\";      \n" +
                "    }\n" +
                "    if(this.$route.meta.type === \"update\"){\n" +
                "      this.allReadOnly = false;\n" +
                "      this.readOnly = true;\n" +
                "    }else if(this.$route.meta.type === \"add\"){\n" +
                "      this.allReadOnly = false;\n" +
                "      this.readOnly = false;\n" +
                "    }\n" +
                "\n" +
                "    //不是添加  获取数据\n" +
                "    if (this.$route.meta.type !== \"add\") {\n" +
                "      this.getData();\n" +
                "    }\n" +
                "  },\n" +
                "\n" +
                "  methods: {\n" +
                "    getDeptList() {\n" +
                "     this.listLoading = true;\n" +
                "     fetchDepartmentList().then(response => {\n" +
                "       this.options = response;\n" +
                "\n" +
                "      });\n" +
                "    },\n" +
                "\n" +
                "  getRoleList() {\n" +
                "      this.listLoading = true;\n" +
                "     fetchRoleList().then(response => {\n" +
                "      this.roleList=response\n" +
                "      \n" +
                "      });\n" +
                "    },\n" +
                "\n" +
                "    getData(){\n" +
                "      this.listLoading = true;\n" +
                "      const id = this.$route.query && this.$route.query.id;\n" +
                "      fetch" + upFileCode + "(id).then(response => {\n" +
                "        this.from = response;\n" +
                "        this.listLoading = false;\n" +
                "      });\n" +
                "      \n" +
                "    },\n" +
                "\n" +
                "\n" +
                "    \n" +
                "   //保存 / 修改\n" +
                "    submitForm() {\n" +
                "      this.$refs.from.validate(valid => {\n" +
                "        if (valid) {\n" +
                "          console.log(this.$route.meta.type);\n" +
                "          if (this.$route.meta.type === \"add\") {\n" +
                "            add" + upFileCode + "(this.from).then(response => {\n" +
                "               this.showResponse(response);\n" +
                "            });\n" +
                "          }else{\n" +
                "            edit" + upFileCode + "(this.from).then(response => {\n" +
                "              console.log(response);\n" +
                "               this.showResponse(response);\n" +
                "            });\n" +
                "          }\n" +
                "\n" +
                "          // this.$notify({\n" +
                "          //   title: \"成功\",\n" +
                "          //   message: \"保存成功\",\n" +
                "          //   type: \"success\",\n" +
                "          //   duration: 2000\n" +
                "          // });\n" +
                "          // this.back();\n" +
                "          \n" +
                "        } else {\n" +
                "          console.log(\"error submit!!\");\n" +
                "          return false;\n" +
                "        }\n" +
                "      });\n" +
                "    },\n" +
                "    //返回\n" +
                "    back() {\n" +
                "      this.$router.push({ path: \"/"+excleColumn.getSuperTreeCode()+"/"+excleColumn.getFileCode()+"\" });\n" +
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
                "          this.back();\n" +
                "        } else {\n" +
                "          this.$notify({\n" +
                "            title: \"失败\",\n" +
                "            message: response.msg,\n" +
                "            type: \"error\",\n" +
                "            duration: 2000\n" +
                "          });\n" +
                "        }\n" +
                "    }\n" +
                "  }\n" +
                "};\n" +
                "</script>";
        outPutStringList.add(1,addStr);
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
                "export function fetchList(query) {\n" +
                "  return request({\n" +
                "    api: 'BAPI',url: '/" + superCode + "/" + code + "Controller/" + code + "List',\n" +
                "    method: 'get',\n" +
                "    params: query\n" +
                "  })\n" +
                "}\n" +
                "\n" +
                "export function fetch" + upCode + "(query) {\n" +
                "  return request({\n" +
                "    api: 'BAPI',url: '/" + superCode + "/" + code + "Controller/" + code + "Detail',\n" +
                "    method: 'get',\n" +
                "    params: query\n" +
                "  })\n" +
                "}\n" +
                "\n" +
                "export function delete" + upCode + "(data) {\n" +
                "  return request({\n" +
                "    api: 'BAPI',url: '/" + superCode + "/" + code + "Controller/del" + upCode + "',\n" +
                "    method: 'delete',\n" +
                "    params:data\n" +
                "  })\n" +
                "}\n" +
                "\n" +
                "export function add" + upCode + "(data) {\n" +
                "  return request({\n" +
                "    api: 'BAPI',url: '/" + superCode + "/" + code + "Controller/" + code + "Add',\n" +
                "    method: 'post',\n" +
                "    data\n" +
                "  })\n" +
                "}\n" +
                "\n" +
                "export function edit" + upCode + "(data) {\n" +
                "  return request({\n" +
                "    api: 'BAPI',url: '/" + superCode + "/" + code + "Controller/" + code + "Edit',\n" +
                "    method: 'post',\n" +
                "    data\n" +
                "  })\n" +
                "}\n" +
                "export function fetchRoleList() {\n" +
                "  return request({\n" +
                "    api: 'BAPI',url: '/" + superCode + "/" + code + "Controller/getRoleList',\n" +
                "    method: 'get',\n" +
                "  })\n" +
                "}";
        outPutStringList.add(2, apiStr);
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
        String upCode = code.substring(0, 1).toUpperCase() + code.substring(1);
        String mockIndex =
                "//" + name + "\n" +
                "Mock.mock(/\\/" + superCode + "\\/" + code + "Controller\\/" + code + "List/, 'get', loginAPI." + code + "List)\n" +
                "Mock.mock(/\\/" + superCode + "\\/" + code + "Controller\\/" + code + "Detail/, 'get', loginAPI." + code + "Detail)   \n" +
                "Mock.mock(/\\/" + superCode + "\\/" + code + "Controller\\/" + code + "Edit/, 'post', loginAPI." + code + "Edit)\n" +
                "Mock.mock(/\\/" + superCode + "\\/" + code + "Controller\\/" + code + "Add/, 'post', loginAPI." + code + "Add)\n" +
                "Mock.mock(/\\/" + superCode + "\\/" + code + "Controller\\/getRoleList/, 'get', loginAPI.getRoleList)   \n";

        String mockLogin = "  //" + name + "\n" +
                "  "+code+"List: config => {\n" +
                "     return {\"pageNum\":1,\"pageSize\":10,\"size\":2,\"startRow\":1,\"endRow\":2,\"total\":2,\"pages\":1,\"list\":[";
        for (int i = 0; i < 5; i++) {
            mockLogin += "{\"pageNum\":1,\"pageSize\":10,\"userId\":\"" + i + "\"";
            for (int j = 0; j < fieldList.size(); j++) {
                Field field =  fieldList.get(j);
                String fieldCode = field.getFieldCode();
                mockLogin += ",\""+fieldCode+"\":\"column"+(j+1)+"\"";
            }
            if (i < 4) {
                mockLogin += "},";
            } else {
                mockLogin += "}";
            }

        }

        mockLogin += "],\"prePage\":0,\"nextPage\":0,\"isFirstPage\":true,\"isLastPage\":true,\"hasPreviousPage\":false,\"hasNextPage\":false,\"navigatePages\":8,\"navigatepageNums\":[1],\"navigateFirstPage\":1,\"navigateLastPage\":1,\"lastPage\":1,\"firstPage\":1}  \n" +
                "  },\n" +
                "\n" +
                "  " + code + "Add:config => {\n" +
                "    return  {\"state\":true,\"msg\":\"增加" + name + "成功\",\"data\":null,\"code\":200}\n" +
                "  },\n" +
                "  " + code + "Edit:config => {\n" +
                "    return  {\"state\":true,\"msg\":\"修改" + name + "成功\",\"data\":null,\"code\":200}\n" +
                "  },\n" +
                "\n" +
                "  "+code+"Detail:config => {\n" +
                "    return {\"pageNum\":1,\"pageSize\":10,\"userId\":\"29\"";
        for (int i = 0; i < fieldList.size(); i++) {
            Field field =  fieldList.get(i);
            String fieldCode = field.getFieldCode();
            mockLogin += ",\"" + fieldCode + "\":\"cloumn" + (i+1) + "\"";
        }
        mockLogin +=       "  }\n";
        mockLogin +=       "  },\n";
        outPutStringList.add(3,mockIndex);
        outPutStringList.add(4,mockLogin);
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
//        String routeString = "{\n" +
//                "    path: '/" + superCode + "',\n" +
//                "    component: Layout,\n" +
//                "    redirect: 'noredirect',\n" +
//                "    name: '" + superCode + "',\n" +
//                "    meta: {\n" +
//                "      title: '" + superName + "',\n" +
//                "      icon: 'chart',\n" +
//                "      menucode :'menuManager'\n" +
//                "    },\n" +
//                "    children : [\n";
        String routeString =
                "//" + name + "\n" +
                "       { path: '" + code + "', component: () => import('@/views/" + superCode + "/" + code + "/list'), name: '" + code + "', meta: { title: '" + name + "', noCache: true,menucode :'menuManager'}},\n" +
                "       { path: '" + code + "/add', component: () => import('@/views/" + superCode + "/" + code + "/add'), name: '" + code + "Add', meta: { title: '" + name + "添加',type:\"add\", noCache: true ,parent:{path:'/" + superCode + "/" + code + "',title:'" + name + "'}},hidden:true},\n" +
                "       { path: '" + code + "/edit', component: () => import('@/views/" + superCode + "/" + code + "/add'), name: '" + code + "Edit', meta: { title: '" + name + "修改',type:\"update\", noCache: true,parent:{path:'/" + superCode + "/" + code + "',title:'" + name + "'}},hidden:true},\n";
//        routeString += "] \n" +
//                "    }\n";
        outPutStringList.add(5,routeString);

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
        String apiDir = "G://src/api/" + superTreeCode;
        String mockDir = "G://src/mock";
        String routeDir = "G://src/route";
        String viewDir = "G://src/view/" + superTreeCode +"/" + code;
        File directory = new File(apiDir);
        if (!directory.exists()) {
            directory.mkdirs();
        }
        directory = new File(mockDir);
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
        File apiFile = new File(apiDir + "/" + code + ".js");
        File mockIndex = new File(mockDir+ "/index.js");
        File mockLogin = new File(mockDir+ "/login.js");
        File routeFile = new File(routeDir+ "/index.js");

        createFile(vueList, outPutStringList.get(0));
        createFile(vueAdd, outPutStringList.get(1));
        createFile(apiFile, outPutStringList.get(2));
        appendFile(mockIndex, outPutStringList.get(3));
        appendFile(mockLogin, outPutStringList.get(4));
        appendFile(routeFile, outPutStringList.get(5));

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
