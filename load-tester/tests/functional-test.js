import { functionalTestScenario } from '../scenarios/functional-scenario.js';

export const options = {
  vus: 10, // 仮想ユーザー数
  duration: '30s', // テスト実行時間
};

export default function () {
  functionalTestScenario();
}