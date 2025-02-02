import { useTheme } from '@mui/styles';
import React, { FunctionComponent } from 'react';
import Chart from 'react-apexcharts';
import * as R from 'ramda';
import Empty from '../../../../../components/Empty';
import type { ExerciseStore } from '../../../../../actions/exercises/Exercise';
import { useFormatter } from '../../../../../components/i18n';
import { useAppDispatch } from '../../../../../utils/hooks';
import { useHelper } from '../../../../../store';
import useDataLoader from '../../../../../utils/hooks/useDataLoader';
import { lineChartOptions } from '../../../../../utils/Charts';
import type { Theme } from '../../../../../components/Theme';
import type { InjectHelper } from '../../../../../actions/injects/inject-helper';
import { fetchInjects } from '../../../../../actions/Inject';
import type { InjectStore } from '../../../../../actions/injects/Inject';

interface Props {
  exerciseId: ExerciseStore['exercise_id'];
}

const InjectOverTimeLine: FunctionComponent<Props> = ({
  exerciseId,
}) => {
  // Standard hooks
  const { t, nsdt } = useFormatter();
  const dispatch = useAppDispatch();
  const theme: Theme = useTheme();

  // Fetching data
  const { injects } = useHelper((helper: InjectHelper) => ({
    injects: helper.getExerciseInjects(exerciseId),
  }));
  useDataLoader(() => {
    dispatch(fetchInjects(exerciseId));
  });

  let cumulation = 0;
  const injectsOverTime = R.pipe(
    R.filter((i: InjectStore) => i && i.inject_sent_at !== null),
    R.sortWith([R.ascend(R.prop('inject_sent_at'))]),
    R.map((i: InjectStore) => {
      cumulation += 1;
      return R.assoc('inject_cumulated_number', cumulation, i);
    }),
  )(injects);
  const injectsData = [
    {
      name: t('Number of injects'),
      data: injectsOverTime.map((i: InjectStore & { inject_cumulated_number: number }) => ({
        x: i.inject_sent_at,
        y: i.inject_cumulated_number,
      })),
    },
  ];

  return (
    <>
      {injectsOverTime.length > 0 ? (
        <Chart
          // @ts-expect-error: Need to migrate Chart.js file
          options={lineChartOptions(
            theme,
            true,
            // @ts-expect-error: Need to migrate i18n.js file
            nsdt,
            null,
            undefined,
            false,
          )}
          series={injectsData}
          type="line"
          width="100%"
          height={350}
        />
      ) : (
        <Empty
          message={t(
            'No data to display or the simulation has not started yet',
          )}
        />
      )}
    </>
  );
};

export default InjectOverTimeLine;
