import React, { FunctionComponent } from 'react';
import { Button, MenuItem } from '@mui/material';
import { Controller, SubmitHandler, useForm } from 'react-hook-form';
import { zodResolver } from '@hookform/resolvers/zod';
import { z } from 'zod';
import { DateTimePicker as MuiDateTimePicker } from '@mui/x-date-pickers';
import { useFormatter } from '../../../../components/i18n';
import TagField from '../../../../components/fields/TagField';
import type { ExerciseCreateInput } from '../../../../utils/api-types';
import { zodImplement } from '../../../../utils/Zod';
import TextField from '../../../../components/fields/TextField';
import SelectField from '../../../../components/fields/SelectField';
import { scenarioCategories } from '../../scenarios/ScenarioForm';

interface Props {
  onSubmit: SubmitHandler<ExerciseCreateInput>;
  handleClose: () => void;
  initialValues?: ExerciseCreateInput;
}

const ExerciseCreationForm: FunctionComponent<Props> = ({
  onSubmit,
  handleClose,
  initialValues = {
    exercise_name: '',
    exercise_category: 'attack-scenario',
    exercise_main_focus: 'incident-response',
    exercise_severity: 'high',
    exercise_subtitle: '',
    exercise_description: '',
    exercise_start_date: '',
    exercise_tags: [],
  },
}) => {
  // Standard hooks
  const { t } = useFormatter();

  const {
    register,
    control,
    handleSubmit,
    formState: { errors, isDirty, isSubmitting },
    setValue,
  } = useForm<ExerciseCreateInput>({
    mode: 'onTouched',
    resolver: zodResolver(
      zodImplement<ExerciseCreateInput>().with({
        exercise_name: z.string().min(1, { message: t('Should not be empty') }),
        exercise_subtitle: z.string().optional(),
        exercise_category: z.string().optional(),
        exercise_main_focus: z.string().optional(),
        exercise_severity: z.string().optional(),
        exercise_description: z.string().optional(),
        exercise_start_date: z.string().optional(),
        exercise_tags: z.string().array().optional(),
      }),
    ),
    defaultValues: initialValues,
  });

  return (
    <form id="exerciseForm" onSubmit={handleSubmit(onSubmit)}>
      <TextField
        variant="standard"
        fullWidth
        label={t('Name')}
        style={{ marginTop: 20 }}
        error={!!errors.exercise_name}
        helperText={errors.exercise_name?.message}
        inputProps={register('exercise_name')}
        InputLabelProps={{ required: true }}
        control={control}
        setValue={setValue}
        askAi={true}
      />
      <SelectField
        variant="standard"
        fullWidth={true}
        name='exercise_category'
        label={t('Category')}
        style={{ marginTop: 20 }}
        error={!!errors.exercise_category}
        control={control}
        defaultValue={initialValues.exercise_category}
      >
        {Array.from(scenarioCategories).map(([key, value]) => (
          <MenuItem key={key} value={key}>
            {t(value)}
          </MenuItem>
        ))}
      </SelectField>
      <SelectField
        variant="standard"
        fullWidth={true}
        name='exercise_main_focus'
        label={t('Main focus')}
        style={{ marginTop: 20 }}
        error={!!errors.exercise_main_focus}
        control={control}
        defaultValue={initialValues.exercise_main_focus}
      >
        <MenuItem key="endpoint-protection" value="endpoint-protection">
          {t('Endpoint Protection')}
        </MenuItem>
        <MenuItem key="web-filtering" value="web-filtering">
          {t('Web Filtering')}
        </MenuItem>
        <MenuItem key="incident-response" value="incident-response">
          {t('Incident Response')}
        </MenuItem>
        <MenuItem key="standard-operating-procedure" value="standard-operating-procedure">
          {t('Standard Operating Procedures')}
        </MenuItem>
        <MenuItem key="crisis-communication" value="crisis-communication">
          {t('Crisis Communication')}
        </MenuItem>
        <MenuItem key="strategic-reaction" value="strategic-reaction">
          {t('Strategic Reaction')}
        </MenuItem>
      </SelectField>
      <SelectField
        variant="standard"
        fullWidth={true}
        name='exercise_severity'
        label={t('Severity')}
        style={{ marginTop: 20 }}
        error={!!errors.exercise_severity}
        control={control}
        defaultValue={initialValues.exercise_severity}
      >
        <MenuItem key="low" value="low">
          {t('Low')}
        </MenuItem>
        <MenuItem key="medium" value="medium">
          {t('Medium')}
        </MenuItem>
        <MenuItem key="high" value="high">
          {t('High')}
        </MenuItem>
        <MenuItem key="critical" value="critical">
          {t('Critical')}
        </MenuItem>
      </SelectField>
      <TextField
        variant="standard"
        fullWidth
        multiline
        rows={2}
        label={t('Description')}
        style={{ marginTop: 20 }}
        error={!!errors.exercise_description}
        helperText={errors.exercise_description?.message}
        inputProps={register('exercise_description')}
        control={control}
        setValue={setValue}
        askAi={true}
      />
      <Controller
        control={control}
        name="exercise_start_date"
        render={({ field }) => (
          <MuiDateTimePicker
            value={field.value ? new Date(field.value) : ''}
            label={t('Start date (optional)')}
            minDateTime={new Date()}
            slotProps={{
              textField: {
                variant: 'standard',
                fullWidth: true,
                style: { marginTop: 20 },
                error: !!errors.exercise_start_date,
                helperText: errors.exercise_start_date && errors.exercise_start_date?.message,
              },
            }}
            onChange={(date) => {
              if (date instanceof Date) {
                field.onChange(date?.toISOString());
              }
            }}
            ampm={false}
            format="yyyy-MM-dd HH:mm:ss"
          />
        )}
      />
      <Controller
        control={control}
        name="exercise_tags"
        render={({ field: { onChange, value } }) => (
          <TagField
            name="exercise_tags"
            label={t('Tags')}
            fieldValue={value ?? []}
            fieldOnChange={onChange}
            errors={errors}
            style={{ marginTop: 20 }}
          />
        )}
      />

      <div style={{ float: 'right', marginTop: 20 }}>
        <Button
          variant="contained"
          onClick={handleClose}
          style={{ marginRight: 10 }}
          disabled={isSubmitting}
        >
          {t('Cancel')}
        </Button>
        <Button
          variant="contained"
          color="secondary"
          type="submit"
          disabled={!isDirty || isSubmitting}
        >
          {t('Create')}
        </Button>
      </div>
    </form>
  );
};

export default ExerciseCreationForm;
