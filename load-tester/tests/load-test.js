import { functionalTestScenario } from '../scenarios/functional-scenario.js';
import { loadTestScenario } from '../scenarios/load-scenario.js';

const generateStages = (start, end, step, duration) => {
  const stages = [];
  for (let target = start; target <= end; target += step) {
    stages.push({ duration: duration, target: target });
  }
  return stages;
};

export const options = {
  stages: generateStages(0, 10000, 100, '5s'), // 0から10,000まで100刻みで増加、各ステージ5秒
  thresholds: {
    http_req_failed: [
      { threshold: 'rate<0.001', abortOnFail: true, delayAbortEval: '10s' } // エラー率が1%未満でない場合、テストを終了
    ],
    http_req_duration: [
      { threshold: 'p(95)<1000', abortOnFail: true, delayAbortEval: '10s' } // 95%のリクエストが1,000ms未満でない場合、テストを終了
    ],
    errors: [
      { threshold: 'count==0', abortOnFail: true } // エラーが発生した場合にテストを終了
    ],
  },
};

export default function () {
  functionalTestScenario();
  loadTestScenario();
}