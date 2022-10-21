# 2022 소프트웨어 공학 팀 프로젝트 

- Java Swing을 활용한 테트리스 게임 개발 
- 1, 2, 3차로 나누어서 요구사항 제시 
- 애자일 방법론 적용 
  - 트렐로의 스크럼 보드로 팀원들과 진행상황 공유, 역할분담 
  - 스프린트 단위로 일정 관리 
  - 깃허브에서 코드 버전과 이슈 및 백로그 관리 
 - JUnit으로 단위 테스트 코드 작성 및 배포까지 

## 게임 모드 선택 

### 개인 모드 

- 일반 모드 
- 아이템 모드 

<div class="mode1">
    <img width="320" src="https://user-images.githubusercontent.com/68090939/197129263-15f1bb2e-99b2-4cf1-abdb-fa773c0ff567.png"/> 
    <img width="320" src="https://user-images.githubusercontent.com/68090939/197129500-51447c48-927b-43ea-ae69-7b59f4abcc31.png"/> 
</div>

### 대전 모드 

- 일반 모드 
- 아이템 모드 
- 시간제한 모드 

<div class="mode2">
    <img width="320" src="https://user-images.githubusercontent.com/68090939/197129753-a0fa5bc8-11b0-4b49-99cd-2c31067562a4.png"/> 
    <img width="320" src="https://user-images.githubusercontent.com/68090939/197129805-5be426c0-80c9-49a7-9bc1-46e5d4f45a66.png"/> 
    <img width="320" src="https://user-images.githubusercontent.com/68090939/197129829-8293e98e-7406-4cc7-a8c5-69b239d6c19d.png"/> 
</div>

## 게임 화면 

### 1P Normal Mode 

<img width="500" src="https://user-images.githubusercontent.com/68090939/197130821-87b28c46-cc76-4d60-a3c0-bbda2abd4a8d.png"/> 

### 1P Item Mode 

[아이템에 대한 요구사항 명세서](https://equable-gourd-e4e.notion.site/85498409bd744aa480b4bce6ef76b6c8)

<div class="item">
    <img width="500" src="https://user-images.githubusercontent.com/68090939/197132006-3bc5117c-ae9c-4a76-bc75-362b27e8ff02.png"/> 
    <img width="500" src="https://user-images.githubusercontent.com/68090939/197132036-7509607e-47f9-43d3-8d06-e4efa6d626cd.png"/> 
</div>

### 2P Normal Mode 

![image](https://user-images.githubusercontent.com/68090939/197132831-adc4557a-b2eb-427b-aba3-1cbfe65b83b1.png)

### 2P Item Mode 

![image](https://user-images.githubusercontent.com/68090939/197135561-7ed81a9c-a11a-4d2f-84ff-9279586f77f9.png)

![image](https://user-images.githubusercontent.com/68090939/197133341-541f6e71-ddb9-47ee-a127-2f8039c4a45d.png)

### 2P Time Attack Mode 

- 100초 시간 제한이 다 끝났을 때, 점수가 더 높은 사람이 승리 

![image](https://user-images.githubusercontent.com/68090939/197133572-602886c7-d2c6-4c74-880f-162068247455.png)

## 설정 화면 

- 난이도 조절 
  - easy: I형 블럭 20% 더 자주 등장, 줄 삭제에 따라 블럭이 떨어지는 속도 20% 감소 
  - hard: I형 블럭 20% 덜 등장, 줄 삭제에 따라 블럭이 떨어지는 속도 20% 증가 
- 프로그램을 종료했다가 다시 실행해도 설정값은 그대로 유지 (파일에 저장) 

<img width="500" src="https://user-images.githubusercontent.com/68090939/197130924-6275c4f8-0793-4d43-982d-7d94a6d4afff.png"/> 

## 스코어보드 

- 점수가 높은 순으로 표시
- 방금 입력된 유저 정보는 강조 표시 
- 일반 모드와 아이템 모드 구분해서 표시 

<div class="score">
  <img width="500" src="https://user-images.githubusercontent.com/68090939/197131446-3c6b2e37-30e4-410a-bbf3-fc6d7b62db50.png"/> 
  <img width="500" src="https://user-images.githubusercontent.com/68090939/197136281-8facfcd1-2cea-48a0-a90a-d421fad39ae9.png"/> 
</div> 
